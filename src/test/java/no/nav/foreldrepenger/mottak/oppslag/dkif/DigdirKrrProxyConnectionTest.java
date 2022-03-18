package no.nav.foreldrepenger.mottak.oppslag.dkif;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(SpringExtension.class)
class DigdirKrrProxyConnectionTest {

    private static MockWebServer mockWebServer;
    private static DigdirKrrProxyConnection digdirKrrProxyConnection;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        var baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        var webClient = WebClient.create();
        var digdirKrrProxyConfig = new DigdirKrrProxyConfig(URI.create(baseUrl), "rest/ping", "rest/v1/person", true);
        digdirKrrProxyConnection = new DigdirKrrProxyConnection(webClient, digdirKrrProxyConfig);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void happycase() {
        var body = """
                        {
                          "personident": "string",
                          "aktiv": true,
                          "kanVarsles": true,
                          "reservert": true,
                          "spraak": "NB",
                          "epostadresse": "string",
                          "epostadresseOppdatert": "2022-03-15T13:10:01.920Z",
                          "mobiltelefonnummer": "string",
                          "mobiltelefonnummerOppdatert": "2022-03-15T13:10:01.920Z",
                          "sikkerDigitalPostkasse": {
                            "adresse": "string",
                            "leverandoerAdresse": "string",
                            "leverandoerSertifikat": "string"
                          }
                        }
                        """;
        mockWebServer.enqueue(new MockResponse()
            .setBody(body)
            .addHeader("Content-Type", "application/json"));

        var målform = digdirKrrProxyConnection.målform();
        assertThat(målform).isEqualTo(Målform.NB);
    }

    @Test
    void defaultMålformBrukesVedTomBody() {
        mockWebServer.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json"));

        var målform = digdirKrrProxyConnection.målform();
        assertThat(målform).isEqualTo(Målform.NB);
    }

    @Test
    void skalHiveExceptionVed4xxFeil() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(404)
            .addHeader("Content-Type", "application/json"));

        assertThrows(WebClientResponseException.NotFound.class, () -> digdirKrrProxyConnection.målform());
    }

    @Test
    void skalHiveExceptionVed5xxFeil() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(500)
            .addHeader("Content-Type", "application/json"));

        assertThrows(WebClientResponseException.InternalServerError.class, () -> digdirKrrProxyConnection.målform());
    }
}
