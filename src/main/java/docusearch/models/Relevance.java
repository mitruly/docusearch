package docusearch.models;

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

    @Override
    public String toString() {
        return "Relevance{" +
            "documentName='" + documentName + '\'' +
            ", resultCount=" + resultCount +
            '}';
    }
}
