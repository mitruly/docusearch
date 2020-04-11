package docusearch;

import docusearch.models.Document;
import docusearch.models.Relevance;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.io.File;
import java.io.IOException;

public class DocumentTest {
    private Document goodDocument;

    public DocumentTest() throws IOException {
        File goodFile = new File(getClass().getResource("/Good_Test_File.txt").getFile());
        String goodFileName = goodFile.getName();
        goodDocument = new Document(goodFile.getPath(), goodFileName, goodFileName.toLowerCase());
    }

    @Test
    public void testProperDocumentCreation() {
        assertEquals(goodDocument.getSortName(), goodDocument.getSortName().toLowerCase());
    }

    @Test(expectedExceptions = IOException.class)
    public void testInvalidFileThrowsException() throws IOException {
        // confirm good document properties
        new Document("/bad/file/Name.txt", "Name.txt", "name.txt");
    }

    @Test
    public void testStringMatch() {
        // simple search is case-sensitive
        assertEquals(goodDocument.stringMatch("case sensitivity test"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));
        assertEquals(goodDocument.stringMatch("Δ"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));
        assertEquals(goodDocument.stringMatch("CaSe sensitiVity tEst"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));

        // test repetition
        assertEquals(goodDocument.stringMatch("backtrack"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 5));
        assertEquals(goodDocument.stringMatch("backtrackbacktrackbacktrack"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));

        // test Unicode
        assertEquals(goodDocument.stringMatch("ユニコード"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));
    }

    @Test
    public void testRegexMatch() {
        assertEquals(goodDocument.regexMatch("CaSe sensitiVity tEst"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));
        assertEquals(goodDocument.regexMatch("Δ"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));
        // case-insensitivity from regex works
        assertEquals(goodDocument.regexMatch("(?i)case sensitivity test"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 2));
        // Unicode sensitivity is disabled by default
        assertEquals(goodDocument.regexMatch("(?i)Δ"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));

        // test repetition
        assertEquals(goodDocument.regexMatch("backtrack"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 5));
        assertEquals(goodDocument.regexMatch("backtrackbacktrackbacktrack"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));

        // test Unicode
        assertEquals(goodDocument.regexMatch("ユニコード"), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));
    }

    @Test
    public void testSearchIndex() {
        assertEquals(goodDocument.searchIndex("CaSe sensitiVity tEst", true), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));
        assertEquals(goodDocument.searchIndex("Δ", true), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));
        // case-insensitivity from regex works
        assertEquals(goodDocument.searchIndex("case sensitivity test", false), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 2));
        assertEquals(goodDocument.searchIndex("Δ", false), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 2));

        // test repetition
        assertEquals(goodDocument.searchIndex("backtrack", false), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 5));
        // backtrack is native by design
        assertEquals(goodDocument.searchIndex("backtrackbacktrackbacktrack", false), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 3));

        // test Unicode
        assertEquals(goodDocument.searchIndex("ユニコード", false), new Relevance(goodDocument.getDocumentName(), goodDocument.getSortName(), 1));
    }
}
