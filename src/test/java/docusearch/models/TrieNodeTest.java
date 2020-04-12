package docusearch.models;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TrieNodeTest {
    @Test
    public void testGetChildCreatesNonExistentChild() {
        TrieNode root = new TrieNode('\0');
        assert(root.getChildren().isEmpty());
        assert(root.getChild('a') != null);
    }

    @Test
    public void testGetChildRetrievesExistingChild() {
        TrieNode root = new TrieNode('\0');
        assert(root.getChildren().isEmpty());
        TrieNode child = root.getChild('a');
        assert(root.getChild('a') != null);
        assertEquals(child, root.getChild('a'));
    }

    @Test
    public void testSearchBacktracks() {
        String searchText = "aaaaa";
        TrieNode backtrack = TrieNode.constructTrie(searchText);
        assertEquals(backtrack.search(searchText, "aa", false).intValue(), 4);
    }

    @Test
    public void testSearchCaseSensitivity() {
        String searchText = "Case case";
        TrieNode caseSensitivity = TrieNode.constructTrie(searchText);
        assertEquals(caseSensitivity.search(searchText, "case", true).intValue(), 1);
        assertEquals(caseSensitivity.search(searchText, "Case", true).intValue(), 1);
        assertEquals(caseSensitivity.search(searchText, "Case", false).intValue(), 2);
    }

    @Test
    public void testSearchPhrase() {
        String searchText = "Testing searching for Phrase";
        TrieNode phrase = TrieNode.constructTrie(searchText);
        assertEquals(phrase.search(searchText, "searching for phrase", false).intValue(), 1);
        // test partial match
        assertEquals(phrase.search(searchText, "ing for phr", false).intValue(), 1);
    }
}
