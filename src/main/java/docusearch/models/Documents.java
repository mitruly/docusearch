package docusearch.models;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.PriorityQueue;

public class Documents {
    private final List<Document> documents;
    private final Integer documentCount;

    public Documents(List<Document> documents) {
        this.documents = documents;
        this.documentCount = documents.size();
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public SearchResults search(String searchText, SearchType searchType) {
        PriorityQueue<Relevance> searchResults = new PriorityQueue<>(new Relevance.RelevanceComparator());

        Instant startTime = Instant.now();
        for (Document document : documents) {
            Relevance relevance = null;
            switch (searchType) {
                case SIMPLE:
                    relevance = document.stringMatch(searchText);
                    break;
                case REGEX:
                    relevance = document.regexMatch(searchText);
                    break;
                case INDEXED:
                    relevance = document.searchIndex(searchText, true);
            }
            if (relevance.getResultCount() > 0) {
                searchResults.add(relevance);
            }
        }
        Instant endTime = Instant.now();

        return new SearchResults(searchText, searchResults, searchResults.size(), documentCount, Duration.between(startTime, endTime).toNanos());
    }
}
