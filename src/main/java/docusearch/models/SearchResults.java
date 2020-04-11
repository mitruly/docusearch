package docusearch.models;

import java.util.PriorityQueue;

public class SearchResults {
    private final PriorityQueue<Relevance> searchResults;
    private final Integer totalResults, totalDocuments;
    private final Long elapsedNanoseconds;

    public SearchResults(
        PriorityQueue<Relevance> searchResults,
        Integer totalResults,
        Integer totalDocuments,
        Long elapsedNanoseconds
    ) {
        this.searchResults = searchResults;
        this.totalResults = totalResults;
        this.totalDocuments = totalDocuments;
        this.elapsedNanoseconds = elapsedNanoseconds;
    }
}
