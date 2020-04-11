package docusearch.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthController {
    @GetMapping("/health")
    ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
