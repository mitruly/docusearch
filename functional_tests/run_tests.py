#!/usr/bin/env python

import atexit
from queue import Queue
import random
import requests
from threading import Event, Thread
import time
from os import listdir
from os.path import isfile, join

from hamcrest import *

BASE_URL = "http://localhost:9000"

## health endpoint testing
def test_health_endpoint_200():
    response = requests.get(BASE_URL + "/health")
    assert_that(response.status_code, is_(200))

## search endpoint testing
def test_search_missing_search_term_fails():
    response = requests.get(BASE_URL + "/search", {'searchType': 'a'})
    assert_that(response.status_code, is_(400))
    assert_that(response.json()['message'], is_("Required String parameter 'searchText' is not present"))

def test_search_missing_search_type_fails():
    response = requests.get(BASE_URL + "/search", {'searchText': 'some search term'})
    assert_that(response.status_code, is_(400))
    assert_that(response.json()['message'], is_("Required SearchType parameter 'searchType' is not present"))

def test_search_invalid_search_type_fails():
    response = requests.get(BASE_URL + "/search", {'searchText': 'some search term', 'searchType': 'invalid search type'})
    assert_that(response.status_code, is_(400))
    assert_that(response.json()['error'], is_("Invalid search type. Valid values include: simple, regex, indexed"))

def test_search_good_request_succeeds():
    response = requests.get(BASE_URL + "/search", {'searchText': 'and', 'searchType': 'indexed'})
    assert_that(response.status_code, is_(200))
    assert_that(response.json(), has_item('searchResults'))

def test_search_uri_encoding_succeeds():
    response = requests.get(BASE_URL + "/search", {'searchText': '&', 'searchType': 'indexed'})
    assert_that(response.status_code, is_(200))
    assert_that(response.json()['totalResults'], is_not(0))

def test_search_with_uri_encoding_succeeds():
    response = requests.get(BASE_URL + "/search", {'searchText': '&', 'searchType': 'indexed'})
    assert_that(response.status_code, is_(200))
    assert_that(response.json()['totalResults'], is_not(0))

def test_search_all_search_types_match_results():
    simple_search_response = requests.get(BASE_URL + "/search", {'searchText': '&', 'searchType': 'simple'})
    assert_that(simple_search_response.status_code, is_(200))
    regex_search_response = requests.get(BASE_URL + "/search", {'searchText': '&', 'searchType': 'regex'})
    assert_that(regex_search_response.status_code, is_(200))
    indexed_search_response = requests.get(BASE_URL + "/search", {'searchText': '&', 'searchType': 'indexed'})
    assert_that(indexed_search_response.status_code, is_(200))

    simple_search_results = simple_search_response.json()['searchResults']
    regex_search_results = regex_search_response.json()['searchResults']
    indexed_search_results = indexed_search_response.json()['searchResults']

    assert_that(simple_search_results, is_(regex_search_results))
    assert_that(simple_search_results, is_(indexed_search_results))

## benchmark methods
# global constants
THREADS = []
ELAPSED_NANOS = {}
SEARCH_TYPES = ['simple', 'regex', 'indexed']
NANOS_IN_MILLIS = 1000000
REGEX_SPECIAL_CHARACTERS = "\^$.|?*+()[]{}"

# modifiable settings
TOTAL_RUNS = 10000
NUM_THREADS = 30
PRINT_THRESHOLD = 1000
PHRASE_MAX_LENGTH = 2 # must be >= 1
DOCUMENT_FOLDER = "../docker/files/"

def test_benchmark_search_types():
    dictionary_words = set()
    prepare_dictionary(dictionary_words)
    dictionary_list = list(dictionary_words) # convert set to list to optimize random.sample

    # prepare search terms for runs
    search_terms = Queue(maxsize = 0)
    for i in range(TOTAL_RUNS):
        phrase_length = random.randint(1, PHRASE_MAX_LENGTH)
        search_terms.put(" ".join(random.sample(dictionary_list, phrase_length)))

    start_time = time.time()

    # register exit event for thread stop
    atexit.register(output, TOTAL_RUNS, NUM_THREADS, start_time)

    run_event = Event()
    run_event.set()

    # initialize nano tracker
    for search_type in SEARCH_TYPES:
        ELAPSED_NANOS[search_type] = [0] * NUM_THREADS

    print("Starting benchmark...")
    start_time = time.time()
    try:
        for x in range(NUM_THREADS):
            THREADS.append(Thread(target=perform_search, args=(search_terms, start_time, x, run_event, PRINT_THRESHOLD)))
            THREADS[x].setDaemon(True)
            THREADS[x].start()

        terminate_threads()

    except KeyboardInterrupt:
        print("Attempting to stop threads..")
        run_event.clear()
        time.sleep(0.5)
        terminate_threads()

def prepare_dictionary(dictionary_words):
    # parse documents for words which are defined by whitespace
    documents = ["{}{}".format(DOCUMENT_FOLDER, file) for file in listdir(DOCUMENT_FOLDER) if isfile(join(DOCUMENT_FOLDER, file))]
    for file in documents:
        with open(file) as f:
            for line in f.readlines():
                line = line.strip() # Remove line endings
                words = line.split(" ")
                for word in words:
                    dictionary_words.add(word)

# search function that executes per thread
def perform_search(search_queue, start_time, thread_number, run_event, print_threshold):
    counter = 0

    while not search_queue.empty() and run_event.is_set():
        search_text = search_queue.get()
        search_queue.task_done() # unblock synchronous queue

        for search_type in SEARCH_TYPES:
            if search_type is "regex":
                normalized_search_text = search_text.translate ({ord(char): "\{}".format(char) for char in REGEX_SPECIAL_CHARACTERS}) # escape special regex characters
            else:
                normalized_search_text = search_text
            response = requests.get(BASE_URL + "/search", {'searchText': normalized_search_text, 'searchType': search_type})
            ELAPSED_NANOS[search_type][thread_number] += response.json()['elapsedNanoseconds']

        counter += 1
        if counter % print_threshold == 0:
            print("Thread {} processed {} requests in {} seconds.".format(thread_number, counter, (time.time() - start_time)))

# exit handler
def output(total_runs, num_threads, start_time):
    end_time = time.time()
    simple_search_total_nanos = 0
    regex_search_total_nanos = 0
    indexed_search_total_nanos = 0

    for x in range(num_threads):
        simple_search_total_nanos += ELAPSED_NANOS['simple'][x]
        regex_search_total_nanos += ELAPSED_NANOS['regex'][x]
        indexed_search_total_nanos += ELAPSED_NANOS['indexed'][x]

    print("Average time (simple search): {} milliseconds".format(simple_search_total_nanos/total_runs/NANOS_IN_MILLIS))
    print("Average time (regex search): {} milliseconds".format(regex_search_total_nanos/total_runs/NANOS_IN_MILLIS))
    print("Average time (indexed search): {} milliseconds".format(indexed_search_total_nanos/total_runs/NANOS_IN_MILLIS))
    print("Total time elapsed: {}".format(time.time() - start_time))

def terminate_threads():
    for x in range(len(THREADS)):
        THREADS[x].join()
    print("All threads stopped gracefully..")

def main():
    test_health_endpoint_200()
    test_search_missing_search_term_fails()
    test_search_missing_search_type_fails()
    test_search_invalid_search_type_fails()
    test_search_good_request_succeeds()
    test_search_with_uri_encoding_succeeds()
    test_search_all_search_types_match_results()
    test_benchmark_search_types()

if __name__ == "__main__":
    main()

