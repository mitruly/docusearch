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
public class HealthControllerTest {
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
    public void testHealthEndpoint() throws IOException {
        wireMockServer.stubFor(
            get(urlEqualTo("/health"))
                .willReturn(
                    aResponse()
                        .withStatus(200)));

        webTestClient.get().uri(baseUrl + "/health").exchange().expectStatus().is2xxSuccessful();
    }
}
