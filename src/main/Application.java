package main;

import main.domain.TrieNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {
    static public String preProcessFile(String file) {
        try {
            StringBuilder sb = new StringBuilder();
            int c;
            boolean spaceAppended = false;
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((c = br.read()) != -1) {
                if ((char) c == '\n' || (char) c == '\r' || (char) c == '\t') {
                    if (!spaceAppended) {
                        sb.append(' ');
                        spaceAppended = true;
                    }
                } else {
                    if ((char) c == ' ') {
                        if (!spaceAppended) {
                            sb.append((char) c);
                            spaceAppended = true;
                        }
                    } else {
                        sb.append((char) c);
                        spaceAppended = false;
                    }
                }
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            System.out.println("Error processing file: " + file);
            return null;
        }
    }

    static public void stringMatch(ArrayList<String> documents, String target) {
        for (String path : documents) {
            String text = preProcessFile(path);

            if (text != null) {
                int count = 0, index = 0;

                while ((index = text.indexOf(target, index)) != -1) {
                    count++;
                    index += target.length();
                }
                System.out.println(new Relevance(path, count));
            }
        }
    }

    static public void regexMatch(ArrayList<String> documents, String regex) {
        for (String path : documents) {
            String text = preProcessFile(path);

            if (text != null) {
                int count = 0;
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(text);
                while (m.find()) {
                    count++;
                }
                System.out.println(new Relevance(path, count));
            }
        }
    }

    static public TrieNode constructTrie(String text) {
        TrieNode root = new TrieNode('\0');
        TrieNode currentNode = root;
        Queue<TrieNode> wordNodes = new LinkedList<>();

        for (int index = 0; index < text.length(); index++) {
            char c = text.charAt(index);

            if (c == ' ') {
                if (currentNode != root) {
                    while (wordNodes.peek() != null) {
                        wordNodes.poll();
                    }
                    currentNode = root;
                }
            } else {
                int queueSize = wordNodes.size();

                for (int i = 0; i < queueSize; i++) {
                    TrieNode t = wordNodes.poll().getChild(c);
                    t.addPosition(index);
                    wordNodes.offer(t);
                }
                currentNode = currentNode.getChild(c);

                TrieNode rootNode = root.getChild(c);
                rootNode.addPosition(index);
                wordNodes.add(rootNode);
            }
        }
        wordNodes.clear();
        return root;
    }

    static public void searchIndex(ArrayList<String> documents, String target) {
        for (String path : documents) {
            String text = preProcessFile(path);

            if (text != null) {
                TrieNode trie = constructTrie(text);
                Integer occurrences = trie.search(text, target);
                if (occurrences != null) {
                    System.out.println(new Relevance(path, occurrences));
                }
            }
        }
    }

    static public void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the search term: ");
        String searchTerm = scanner.nextLine();

        ArrayList<String> documentPaths = new ArrayList<>();
        documentPaths.add("/Users/mly856/Documents/Github/document-search/files/french_armed_forces.txt");
        documentPaths.add("/Users/mly856/Documents/Github/document-search/files/hitchhikers.txt");
        documentPaths.add("/Users/mly856/Documents/Github/document-search/files/warp_drive.txt");
        documentPaths.add("/Users/mly856/Documents/Github/document-search/files/test_file.txt");

        boolean searchMethodValid = false;
        int searchMethod = 0;
        while (!searchMethodValid) {
            System.out.print("Search method: 1) String Match 2) Regular Expression 3) Indexed ");
            searchMethod = scanner.nextInt();

            if (searchMethod > 0 && searchMethod <= 3) searchMethodValid = true;
        }

        if (searchMethod == 1) {
            stringMatch(documentPaths, searchTerm);
        } else if (searchMethod == 2) {
            regexMatch(documentPaths, searchTerm);
        } else {
            searchIndex(documentPaths, searchTerm);
        }
    }
}
