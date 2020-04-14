package docusearch.models;

import docusearch.utils.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class TrieNode {
    private HashMap<Character, TrieNode> children; // Link to contiguous characters
    private Character value; // Not really needed but useful for debugging
    private ArrayList<Integer> positions; // Position of character in document

    public TrieNode(Character c) {
        this.children = new HashMap<>();
        this.positions = new ArrayList<>();
        this.value = c;
    }

    public ArrayList<Integer> getPositions() {
        return positions;
    }

    public HashMap<Character, TrieNode> getChildren() {
        return children;
    }

    public void addPosition(Integer position) {
        positions.add(position);
    }

    public TrieNode getChild(Character c) {
        TrieNode t;
        if ((t = children.get(c)) == null) {
            t = new TrieNode(c);
            children.put(c, t);
        }
        return t;
    }

    /*
     * Supports lookup by single term or phrase
     */
    public Integer search(String searchText, String target, boolean isCaseSensitive) {
        TrieNode t = this;
        int targetPtr = 0, counter = 0;
        ArrayList<Integer> positions = null;

        while (targetPtr < target.length()) {
            char c = Character.toLowerCase(target.charAt(targetPtr));

            if (target.charAt(targetPtr) == ' ') {
                positions = t.positions;
                break;
            }

            if ((t = t.children.get(c)) == null) {
                return 0;
            }
            targetPtr++;
        }

        int wordLength = targetPtr;

        if (targetPtr == target.length()) {
            if (isCaseSensitive) {
                for (int stringEnd : t.positions) {
                    if (StringHelper.subStringsMatch(target, 0, searchText, stringEnd - (wordLength - 1), wordLength)) {
                        counter++;
                    }
                }
            } else {
                counter = t.positions.size();
            }
        } else {
            for (int stringEnd : positions) {
                // for case-sensitivity, we need to back-track
                if (isCaseSensitive && !StringHelper.subStringsMatch(target, 0, searchText, stringEnd - (wordLength - 1), wordLength)) {
                    continue;
                }

                int searchTextPtr = stringEnd + 1;
                targetPtr = wordLength;

                while (searchTextPtr < searchText.length() && targetPtr < target.length() && StringHelper.characterMatch(searchText.charAt(searchTextPtr), target.charAt(targetPtr), isCaseSensitive)) {
                    searchTextPtr++;
                    targetPtr++;
                }

                if ((wordLength + (searchTextPtr - stringEnd - 1)) == target.length()) {
                    counter++;
                }
            }
        }

        return counter;
    }

    public Character getValue() {
        return value;
    }

    static public TrieNode constructTrie(String text) {
        TrieNode root = new TrieNode('\0');
        TrieNode currentNode = root;
        Queue<TrieNode> wordNodes = new LinkedList<>();

        for (int index = 0; index < text.length(); index++) {
            char c = Character.toLowerCase(text.charAt(index));

            // identify word boundaries
            if (c == ' ') {
                if (currentNode != root) {
                    while (wordNodes.peek() != null) {
                        wordNodes.poll();
                    }

                    // restart new words at the root
                    currentNode = root;
                }
            } else {
                int queueSize = wordNodes.size();

                // for each word, add all partial suffixes to the root
                for (int i = 0; i < queueSize; i++) {
                    TrieNode t = wordNodes.poll().getChild(c);
                    t.addPosition(index);
                    wordNodes.offer(t);
                }
                currentNode = currentNode.getChild(c);

                TrieNode rootNode = root.getChild(c);
                rootNode.addPosition(index);
                wordNodes.offer(rootNode);
            }
        }
        wordNodes.clear();
        return root;
    }

    @Override
    public String toString() {
        return "TrieNode{" +
            "value=" + value +
            ", positions=" + positions +
            ", children=" + children +
            '}';
    }
}
