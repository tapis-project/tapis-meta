package edu.utexas.tacc.tapis.meta;

import com.fasterxml.jackson.core.type.TypeReference;
import edu.utexas.tacc.tapis.auth.client.AuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletionException;


/**
 * These tests assume that there is already a database created named "localdb" and testuser2
 * has full access to that database.
 *
 */
@Test
public class ITestEndpoints {

    private static final Logger log = LoggerFactory.getLogger(ITestEndpoints.class);
    private String userJwt;
    private final HttpClient client = HttpClient.newHttpClient();
    private static final UncheckedObjectMapper objectMapper = new UncheckedObjectMapper();


    // Runs once per test suite
    @BeforeTest
    private void getToken() throws Exception {
        AuthClient authClient = new AuthClient("https://dev.develop.tapis.io");
        userJwt = authClient.getToken("testuser2", "testuser2");
    }

    // Runs before each test method
    @BeforeMethod
    private void before() throws Exception {
        log.debug("before()");
    }


    public void testHealthCheck() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/healthcheck"))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertNotNull(response.body());

    }


    public void testListCollections() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .header("x-tapis-token", userJwt)
            .uri(URI.create("http://localhost:8080/localdb/"))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertNotNull(response.body());

    }

    public void testCreateCollection() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .header("x-tapis-token", userJwt)
            .uri(URI.create("http://localhost:8080/localdb/testCollection"))
            .PUT(HttpRequest.BodyPublishers.ofString(""))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertTrue(response.statusCode() >= 200);
        Assert.assertNotNull(response.body());

    }

    public void testAddDocument() throws Exception {
        String payload = "{\"name\": \"test\", \"value\":42}";
        HttpRequest request = HttpRequest.newBuilder()
            .header("content-type", "application/json")
            .header("x-tapis-token", userJwt)
            .uri(URI.create("http://localhost:8080/localdb/testCollection/"))
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertTrue(response.statusCode() >= 200);
        Assert.assertNotNull(response.body());



    }

}

class UncheckedObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {
    /**
     * Parses the given JSON string into a Map.
     */
    Map<String, String> readValue(String content) {
        try {
            return this.readValue(content, new TypeReference<>() {
            });
        } catch (IOException ioe) {
            throw new CompletionException(ioe);
        }
    }
}
