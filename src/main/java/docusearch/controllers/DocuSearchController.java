package docusearch.controllers;


import com.google.gson.Gson;
import docusearch.models.DocuSearchError;
import docusearch.models.Documents;
import docusearch.models.SearchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DocuSearchController {
    @Autowired
    private Documents _documents;
    @Autowired Gson gson;

    @GetMapping("/search")
    public ResponseEntity<String> search(@RequestParam String searchText, @RequestParam SearchType searchType) {
        if (searchType == SearchType.INVALID) {
            return new ResponseEntity<>(gson.toJson(new DocuSearchError("Invalid search type. Valid values include: simple, regex, indexed")), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(gson.toJson(_documents.search(searchText, searchType)), HttpStatus.OK);
        }
    }
}
