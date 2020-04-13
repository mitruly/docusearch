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
            String singleFileSearchText = "test";
            SearchResults singleFileResults = documents.search(singleFileSearchText, searchType);
            assertEquals(singleFileResults.getSearchText(), singleFileSearchText);
            assertEquals(singleFileResults.getTotalDocuments().intValue(), 3);
            assertEquals(singleFileResults.getSearchResults().size(), 1);
            assertThat(singleFileResults.getSearchResults(), hasItem(new Relevance(goodDocumentName, goodDocumentName.toLowerCase(), 3)));
            assertEquals(singleFileResults.getTotalResults().intValue(), 1);

            // search result in multiple files
            String multipleFileSearchText = "and";
            SearchResults multipleFileResults = documents.search(multipleFileSearchText, searchType);
            assertEquals(multipleFileResults.getTotalDocuments().intValue(), 3);

            // check for ordering
            assertEquals(multipleFileResults.getSearchText(), multipleFileSearchText);
            PriorityQueue<Relevance> multipleFileSearchResults = multipleFileResults.getSearchResults();
            assertEquals(multipleFileSearchResults.size(), 3);
            assertEquals(multipleFileSearchResults.poll(), new Relevance(anotherFileName, anotherFileName.toLowerCase(), 5));
            assertEquals(multipleFileSearchResults.poll(), new Relevance(nurseryDocumentName, nurseryDocumentName.toLowerCase(), 4));
            assertEquals(multipleFileSearchResults.poll(), new Relevance(goodDocumentName, goodDocumentName.toLowerCase(), 4));
            assertEquals(multipleFileResults.getTotalResults().intValue(), 3);

            // search result in no files
            String noFileSearchText = "doesn't exist";
            SearchResults noFileResults = documents.search(noFileSearchText, searchType);
            assertEquals(noFileResults.getSearchText(), noFileSearchText);
            assertEquals(noFileResults.getTotalDocuments().intValue(), 3);
            assertEquals(noFileResults.getSearchResults().size(), 0);
        }
    }
}
