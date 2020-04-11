package docusearch.models;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Documents {
    private final List<Document> _documents;

    public Documents(List<Document> documents) {
        this._documents = documents;
    }

    public SearchResults search(String searchText, SearchTypes searchTypes) {
        PriorityQueue<Relevance> searchResults = new PriorityQueue<>(new Comparator<Relevance>() {
            @Override
            public int compare(Relevance o1, Relevance o2) {
                // Sort relevance from largest to smallest, then by document name
                if (o1.getResultCount().equals(o2.getResultCount())) {
                    return o1.getDocumentName().compareTo(o2.getDocumentName());
                } else {
                    if (o1.getResultCount() < o2.getResultCount()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        });

        for (Document document : _documents) {
            Relevance relevance = null;
            switch (searchTypes) {
                case SIMPLE:
                    relevance = document.stringMatch(searchText);
                    break;
                case REGEX:
                    relevance = document.regexMatch(searchText);
                    break;
                case INDEXED:
                    relevance = document.searchIndex(searchText);
            }
            if (relevance.getResultCount() > 0) {
                searchResults.add(relevance);
            }
        }

        return new SearchResults(searchResults, searchResults.size());
    }
}
