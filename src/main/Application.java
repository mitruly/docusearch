package main;

import main.domain.Document;

import java.util.*;

public class Application {
    static public void stringMatch(ArrayList<String> documents, String target) {
        for (String path : documents) {
            Document document = new Document(path);

            document.stringMatch(target);
        }
    }

    static public void regexMatch(ArrayList<String> documents, String regex) {
        for (String path : documents) {
            Document document = new Document(path);

            document.regexMatch(regex);
        }
    }

    static public void searchIndex(ArrayList<String> documents, String target) {
        for (String path : documents) {
            Document document = new Document(path);

            document.searchIndex(target);
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
