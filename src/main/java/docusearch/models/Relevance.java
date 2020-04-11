package docusearch.models;

import java.util.Objects;

public class Relevance {
    private final String documentName;
    private final transient String sortName; // don't serialize into JSON response
    private final Integer resultCount;

    public Relevance(
        String documentName,
        String sortName,
        Integer resultCount
    ) {
        this.documentName = documentName;
        this.sortName = sortName;
        this.resultCount = resultCount;
    }

    public String getDocumentName() {
        return documentName;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public String getSortName() {
        return sortName;
    }

    @Override
    public String toString() {
        return "Relevance{" +
            "documentName='" + documentName + '\'' +
            ", resultCount=" + resultCount +
            '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentName, resultCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relevance that = (Relevance) o;
        if (!documentName.equals(that.documentName)) return false;
        if (!resultCount.equals(that.resultCount)) return false;
        return true;
    }
}
