package docusearch.models;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {
    private TrieNode preProcess;
    private final String formattedFile, documentName, sortName;
    private final Log logger = LogFactory.getLog(this.getClass());

    public Document(String filePath, String documentName) throws IOException {
        this.documentName = documentName;
        this.sortName = documentName.toLowerCase();

        // TODO: pull pre-processed trie from database if it exists
        logger.info("Pre-processing file: " + documentName);

        // otherwise, pre-process document and store result
        formattedFile = preProcessFile(filePath);
        logger.info("Successfully pre-processed file: " + documentName);
        preProcess = TrieNode.constructTrie(formattedFile);
    }

    private String preProcessFile(String file) throws IOException {
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
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getSortName() {
        return sortName;
    }

    public Relevance stringMatch(String target) {
        int count = 0, index = 0;

        while ((index = formattedFile.indexOf(target, index)) != -1) {
            count++;
            index += target.length();
        }
        return new Relevance(documentName, sortName, count);
    }

    public Relevance regexMatch(String regex) {
        int count = 0;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(formattedFile);
        while (m.find()) {
            count++;
        }
        return new Relevance(documentName, sortName, count);
    }

    public Relevance searchIndex(String target, boolean isCaseSensitive) {
        Integer occurrences = preProcess.search(formattedFile, target, isCaseSensitive);
        return new Relevance(documentName, sortName, occurrences);
    }
}
