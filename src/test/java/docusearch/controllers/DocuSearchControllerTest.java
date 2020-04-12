package docusearch.controllers;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DocuSearchControllerTest {
    private String baseUrl;
    private WireMockServer wireMockServer;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeSuite
    public void beforeAll() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();

        baseUrl = "http://localhost:" + wireMockServer.port();

        webTestClient = WebTestClient.bindToServer().baseUrl(baseUrl).build();
    }

    @AfterSuite
    public void afterAll() {
        wireMockServer.shutdown();
    }

    @Test
    public void testSearchWithoutSearchTextFails() throws IOException {
        String path = "/search?searchType=simple";
        wireMockServer.stubFor(
            get(urlEqualTo(path))
                .willReturn(
                    aResponse()
                        .withStatus(400)));

        webTestClient.get().uri(path).exchange().expectStatus().isBadRequest();
    }

    @Test
    public void testSearchWithoutSearchTypeFails() throws IOException {
        String path = "/search?searchText=someSearchText";
        wireMockServer.stubFor(
            get(urlEqualTo(path))
                .willReturn(
                    aResponse()
                        .withStatus(400)));

        webTestClient.get().uri(path).exchange().expectStatus().isBadRequest();
    }

    @Test
    public void testSearchGoodRequestSucceeds() throws IOException {
        String path = "/search?searchText=someSearchText&searchType=simple";
        wireMockServer.stubFor(
            get(urlEqualTo(path))
                .willReturn(
                    aResponse()
                        .withStatus(200)));

        webTestClient.get().uri(path).exchange().expectStatus().isOk();
    }
}
