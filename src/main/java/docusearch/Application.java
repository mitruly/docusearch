package docusearch;

import com.google.gson.Gson;
import docusearch.models.Document;
import docusearch.models.Documents;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableAsync
public class Application{
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Value("${documents.folder}")
    private String documentFolder;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // recursively populate documents
    public void loadDirectory(List<Document> documents, File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                loadDirectory(documents, fileEntry);
            } else {
                try {
                    String documentName = fileEntry.getName();
                    documents.add(new Document(fileEntry.getAbsolutePath(), documentName, documentName.toLowerCase()));
                } catch (IOException e) {
                    logger.error("Encountered error loading file: " + fileEntry.getAbsolutePath());
                }
            }
        }
    }

    // recursively load documents from folder
    @Bean
    public Documents loadDocuments() {
        logger.info("Loading documents from '" + documentFolder + "'...");
        final File fileFolder = new File(documentFolder);
        List<Document> documentList = new ArrayList<>();

        loadDirectory(documentList, fileFolder);

        return new Documents(documentList);
    }

    // gson serializer/deserializer to be used with components
    @Bean
    public Gson createGson() {
        return new Gson();
    }
}
