package main;

public class Relevance {
    private final String documentName;
    private final Integer resultCount;

    public Relevance(
        String documentName,
        Integer resultCount
    ) {
        this.documentName = documentName;
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
