package docusearch.models;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
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

    public SearchResults search(String searchText, SearchTypes searchTypes) {
        PriorityQueue<Relevance> searchResults = new PriorityQueue<>(new Comparator<Relevance>() {
            @Override
            public int compare(Relevance o1, Relevance o2) {
                // Sort relevance from largest to smallest, then by document name
                if (o1.getResultCount().equals(o2.getResultCount())) {
                    return o1.getSortName().compareTo(o2.getSortName());
                } else {
                    if (o1.getResultCount() < o2.getResultCount()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        });

        Instant startTime = Instant.now();
        for (Document document : documents) {
            Relevance relevance = null;
            switch (searchTypes) {
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

        return new SearchResults(searchResults, searchResults.size(), documentCount, Duration.between(startTime, endTime).toNanos());
    }
}
