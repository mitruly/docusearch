package main.domain;

import java.util.ArrayList;
import java.util.HashMap;

public class TrieNode {
    private HashMap<Character, TrieNode> children;
    private boolean wordEnd;
    private int total;
    private ArrayList<Integer> positions;

    public TrieNode(Character c) {
        this.wordEnd = false;
        this.total = 0;
        this.children = new HashMap<>();
        this.positions = new ArrayList<>();
    }

    public void setWordEnd(int index) {
        this.wordEnd = true;
        this.total++;
        this.positions.add(index);
    }

    public int getTotal() {
        return total;
    }

    public ArrayList<Integer> getPositions() {
        return positions;
    }

    public TrieNode getChild(Character c) {
        TrieNode t;
        if ((t = children.get(c)) == null) {
            t = new TrieNode(c);
            children.put(c, t);
        }
        return t;
    }

    public Integer search(String s) {
        TrieNode t = this;
        for (int i = 0; i < s.length(); i++) {
            if ((t = t.children.get(s.charAt(i))) == null) {
                return null;
            }
        }
        return t.total;
    }

    @Override
    public String toString() {
        return "TrieNode{" +
            "children=" + children +
            ", wordEnd=" + wordEnd +
            ", total=" + total +
            ", positions=" + positions +
            '}';
    }
}
