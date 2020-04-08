package main.domain;

import java.util.ArrayList;
import java.util.HashMap;

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
    public Integer search(String searchText, String target) {
        TrieNode t = this;
        int targetPtr = 0, counter = 0;
        ArrayList<Integer> positions = null;

        while (targetPtr < target.length()) {
            char c = target.charAt(targetPtr);

            if (target.charAt(targetPtr) == ' ') {
                positions = t.positions;
                break;
            }

            if ((t = t.children.get(c)) == null) {
                System.out.println("Returning due to null search");
                return null;
            }
            targetPtr++;
        }

        int wordLength = targetPtr;

        if (targetPtr == target.length()) {
            counter = t.positions.size();
        } else {
            for (int stringEnd : positions) {
                int searchTextPtr = stringEnd + 1;
                targetPtr = wordLength;

                while (searchTextPtr < searchText.length() && targetPtr < target.length() && searchText.charAt(searchTextPtr) == target.charAt(targetPtr)) {
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

    @Override
    public String toString() {
        return "TrieNode{" +
            "value=" + value +
            ", positions=" + positions +
            ", children=" + children +
            '}';
    }
}
