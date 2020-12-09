package client;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java8.En;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class HttpRetryStep implements En {

    private final int port = 18089;
    private HttpClient httpClient;
    private String state;
    private String result;
    private WireMockServer wireMockServer;

    public HttpRetryStep() throws URISyntaxException {
        final String httpScheme = "http";
        final String localHost = "localhost";
        final String testResource = "/my/resource";
        final String scenario = "retryScenario";
        final URI uri = new URIBuilder()
                .setScheme(httpScheme)
                .setHost(localHost)
                .setPort(port)
                .setPath(testResource)
                .build();

        setup();

        Given("I have a http client with {int} number of retries", (Integer retries) -> {
            httpClient = new HttpFactory()
                    .httpClient(retries, 1000, 1000);
        });

        When("i fail {int} with {int} and {string}", (Integer numberFails, Integer httpCode, String body) -> {
            IntStream.rangeClosed(1, numberFails).forEach( i -> {
                String value = String.format("%d time requested", i);
                wireMockServer.stubFor(get(urlEqualTo(testResource)).inScenario(scenario)
                        .whenScenarioStateIs(state)
                        .willSetStateTo(value)
                        .willReturn(
                                aResponse()
                                        .withStatus(httpCode)
                                        .withBody(body)
                        ));
                state = value;
            });
        });

        When("have success with {string}", (String body) -> {
            wireMockServer.stubFor(get(urlEqualTo(testResource)).inScenario(scenario)
                    .whenScenarioStateIs(state)
                    .willSetStateTo("finish")
                    .willReturn(
                            aResponse()
                                    .withStatus(200)
                                    .withBody("success")
                    ));
            result = convertHttpResponseToString(httpClient.execute(new HttpGet(uri)));
        });

        When("have failure with {string}", (String string) -> {
            wireMockServer.stubFor(get(urlEqualTo(testResource)).inScenario(scenario)
                    .whenScenarioStateIs(state)
                    .willSetStateTo("finish")
                    .willReturn(
                            aResponse()
                                    .withStatus(500)
                                    .withBody(string)
                    ));
            result = convertHttpResponseToString(httpClient.execute(new HttpGet(uri)));
        });

        Then("the result should be {string}", (String expected) -> {
            assertEquals(expected, result);
        });

        Then("endpoint should be called {int} times", (Integer expectedNumberOfCalls) -> {
            wireMockServer.verify(expectedNumberOfCalls, getRequestedFor(urlEqualTo(testResource)));
        });
    }

    private void setup() {
        Before(() -> {
            wireMockServer = new WireMockServer(port);
            wireMockServer.start();
        });

        After(() -> wireMockServer.stop());
    }

    private String convertHttpResponseToString(HttpResponse httpResponse) throws IOException {
        InputStream inputStream = httpResponse.getEntity().getContent();
        return convertInputStreamToString(inputStream);
    }

    private String convertInputStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        String string = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return string;
    }
}
