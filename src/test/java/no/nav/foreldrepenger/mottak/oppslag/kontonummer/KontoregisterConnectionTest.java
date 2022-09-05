package no.nav.foreldrepenger.mottak.oppslag.kontonummer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(SpringExtension.class)
class KontoregisterConnectionTest {

    private static MockWebServer mockWebServer;
    private static KontoregisterConnection kontoregisterConnection;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        var baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        var webClient = WebClient.create();
        var kontoregisterConfig = new KontoregisterConfig(URI.create(baseUrl), "/", true);
        kontoregisterConnection = new KontoregisterConnection(webClient, kontoregisterConfig);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void happycase() {
        var body = """
                  {
                      "kontonummer": "8361347234732292",
                      "utenlandskKontoInfo": {
                        "banknavn": "string",
                        "bankkode": "CC123456789",
                        "bankLandkode": "SE",
                        "valutakode": "SEK",
                        "swiftBicKode": "SHEDNO22",
                        "bankadresse1": "string",
                        "bankadresse2": "string",
                        "bankadresse3": "string"
                      }
                    }
                        """;
        mockWebServer.enqueue(new MockResponse()
            .setBody(body)
            .addHeader("Content-Type", "application/json"));

        var kontoinformasjon = kontoregisterConnection.kontonrFraNyTjeneste();
        assertThat(kontoinformasjon).isNotNull();
        assertThat(kontoinformasjon.kontonummer()).isEqualTo("8361347234732292");
    }

    @Test
    void ingenAktiveKontoerBareUtenlandskInfo() {
        var body = """
                  {
                      "utenlandskKontoInfo": {
                        "banknavn": "string",
                        "bankkode": "CC123456789",
                        "bankLandkode": "SE",
                        "valutakode": "SEK",
                        "swiftBicKode": "SHEDNO22",
                        "bankadresse1": "string",
                        "bankadresse2": "string",
                        "bankadresse3": "string"
                      }
                    }
                        """;
        mockWebServer.enqueue(new MockResponse()
            .setBody(body)
            .addHeader("Content-Type", "application/json"));

        var kontoinformasjon = kontoregisterConnection.kontonrFraNyTjeneste();
        assertThat(kontoinformasjon).isNotNull();
        assertThat(kontoinformasjon.kontonummer()).isNull();
        assertThat(kontoinformasjon.utenlandskKontoInfo()).isNull();
    }
}
