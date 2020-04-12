package docusearch.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class DocumentsTest {
    private final Documents documents;
    private final String goodDocumentName, nurseryDocumentName, anotherFileName;

    public DocumentsTest() throws IOException {
        List<Document> documentList = new ArrayList<>();
        File goodFile = new File(getClass().getResource("/Good_Test_File.txt").getFile());
        File nurseryFile = new File(getClass().getResource("/a_nursery_rhyme.txt").getFile());
        File anotherFile = new File(getClass().getResource("/another_test_file.txt").getFile());

        goodDocumentName = goodFile.getName();
        nurseryDocumentName = nurseryFile.getName();
        anotherFileName = anotherFile.getName();
        documentList.add(new Document(goodFile.getPath(), goodDocumentName));
        documentList.add(new Document(nurseryFile.getPath(), nurseryDocumentName));
        documentList.add(new Document(anotherFile.getPath(), anotherFileName));
        this.documents = new Documents(documentList);
    }

    @Test
    void testSimpleSearchSort() {
        List<SearchType> testSearchTypes = new ArrayList<>();
        testSearchTypes.add(SearchType.SIMPLE);
        testSearchTypes.add(SearchType.REGEX);
        testSearchTypes.add(SearchType.INDEXED);

        for (SearchType searchType : testSearchTypes) {
            // search result in single file
            SearchResults singleFileResults = documents.search("test", searchType);
            assertEquals((int) singleFileResults.getTotalDocuments(), 3);
            assertEquals(singleFileResults.getSearchResults().size(), 1);
            assertThat(singleFileResults.getSearchResults(), hasItem(new Relevance(goodDocumentName, goodDocumentName.toLowerCase(), 3)));
            assertEquals((int) singleFileResults.getTotalResults(), 1);

            // search result in multiple files
            SearchResults multipleFileResults = documents.search("and", searchType);
            assertEquals((int) multipleFileResults.getTotalDocuments(), 3);

            // check for ordering
            PriorityQueue<Relevance> multipleFileSearchResults = multipleFileResults.getSearchResults();
            assertEquals(multipleFileSearchResults.size(), 3);
            assertEquals(multipleFileSearchResults.poll(), new Relevance(anotherFileName, anotherFileName.toLowerCase(), 5));
            assertEquals(multipleFileSearchResults.poll(), new Relevance(nurseryDocumentName, nurseryDocumentName.toLowerCase(), 4));
            assertEquals(multipleFileSearchResults.poll(), new Relevance(goodDocumentName, goodDocumentName.toLowerCase(), 4));
            assertEquals((int) multipleFileResults.getTotalResults(), 3);

            // search result in no files
            SearchResults noFileResults = documents.search("doesn't exist", searchType);
            assertEquals((int) noFileResults.getTotalDocuments(), 3);
            assertEquals(noFileResults.getSearchResults().size(), 0);
        }
    }
}
