package main.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {
    private TrieNode preProcess;
    private String formattedFile, path;

    public Document(String path) {
        this.path = path;
        // if data is initialized, fetch from db

        // otherwise, pre-process document and store result
        formattedFile = preProcessFile(path);
        preProcess = TrieNode.constructTrie(formattedFile);
    }

    private String preProcessFile(String file) {
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

    public void stringMatch(String target) {
        if (formattedFile != null) {
            int count = 0, index = 0;

            while ((index = formattedFile.indexOf(target, index)) != -1) {
                count++;
                index += target.length();
            }
            System.out.println(new Relevance(path, count));
        }
    }

    public void regexMatch(String regex) {
        if (formattedFile != null) {
            int count = 0;
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(formattedFile);
            while (m.find()) {
                count++;
            }
            System.out.println(new Relevance(path, count));
        }
    }

    public void searchIndex(String target) {
        if (formattedFile != null) {
            TrieNode trie = TrieNode.constructTrie(formattedFile);
            Integer occurrences = trie.search(formattedFile, target, true);
            if (occurrences != null) {
                System.out.println(new Relevance(path, occurrences));
            }
        }
    }
}
