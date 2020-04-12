package docusearch.models;

import java.io.Serializable;
import java.util.Comparator;
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
        return resultCount.equals(that.resultCount);
    }

    public static class RelevanceComparator implements Comparator<Relevance>, Serializable {
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
    }
}
