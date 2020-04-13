package docusearch.models;

import java.util.Objects;
import java.util.PriorityQueue;

public class SearchResults {
    private final PriorityQueue<Relevance> searchResults;
    private final String searchText;
    private final Integer totalResults, totalDocuments;
    private final Long elapsedNanoseconds;

    public SearchResults(
        String searchText,
        PriorityQueue<Relevance> searchResults,
        Integer totalResults,
        Integer totalDocuments,
        Long elapsedNanoseconds
    ) {
        this.searchText = searchText;
        this.searchResults = searchResults;
        this.totalResults = totalResults;
        this.totalDocuments = totalDocuments;
        this.elapsedNanoseconds = elapsedNanoseconds;
    }

    public String getSearchText() {
        return searchText;
    }

    public PriorityQueue<Relevance> getSearchResults() {
        return searchResults;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public Integer getTotalDocuments() {
        return totalDocuments;
    }

    public Long getElapsedNanoseconds() {
        return elapsedNanoseconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchText, searchResults, totalResults, totalDocuments, elapsedNanoseconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchResults that = (SearchResults) o;
        if (!searchText.equals(that.searchText)) return false;
        if (!searchResults.equals(that.searchResults)) return false;
        if (!totalResults.equals(that.totalResults)) return false;
        if (!totalDocuments.equals(that.totalDocuments)) return false;
        return elapsedNanoseconds.equals(that.elapsedNanoseconds);
    }
}
