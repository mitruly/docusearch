package docusearch.models;

import java.util.PriorityQueue;

public class SearchResults {
    private final PriorityQueue<Relevance> searchResults;
    private final Integer totalResults;

    public SearchResults(
        PriorityQueue<Relevance> searchResults,
        Integer totalResults
    ) {
        this.searchResults = searchResults;
        this.totalResults = totalResults;
    }
}
