package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontonummerConfig;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontonummerConnection;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontoregisterConfig;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontoregisterConnection;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(SpringExtension.class)
class KontonummerHentTest {

    private static MockWebServer mockWebServer;
    private static PDLConnection pdlConnection;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        var baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        var webClient = WebClient.create();
        var kontoregisterConfig = new KontoregisterConfig(URI.create(baseUrl), "/", true);
        var kontoregisterConnection = new KontoregisterConnection(webClient, kontoregisterConfig);

        var kontonummerConfig = new KontonummerConfig("/", true,URI.create(baseUrl));
        var kontonummerConnection = new KontonummerConnection(webClient, kontonummerConfig);

        pdlConnection = new PDLConnection(null, null, null, null,
            kontonummerConnection, kontoregisterConnection, null, null);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void happycase() {
        String bodyFraNyttEndepunkt = happyCaseBodyFraNyttEndepunkt();
        var bodyFpsoknadOppslag = """
            {
              "kontonummer": "123456789",
              "banknavn": "DNB FAKE"
            }
                        """;
        mockWebServer.enqueue(new MockResponse()
            .setBody(bodyFpsoknadOppslag)
            .addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse()
            .setBody(bodyFraNyttEndepunkt)
            .addHeader("Content-Type", "application/json"));

        var bankkonto = pdlConnection.kontonr();
        assertThat(bankkonto.kontonummer()).isEqualTo("123456789");
        assertThat(bankkonto.banknavn()).isEqualTo("DNB FAKE");
    }


    @Test
    void oppslagFeiler() {
        var body = happyCaseBodyFraNyttEndepunkt();
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        mockWebServer.enqueue(new MockResponse()
            .setBody(body)
            .addHeader("Content-Type", "application/json"));


        var bankkonto = pdlConnection.kontonr();
        assertThat(bankkonto).isEqualTo(Bankkonto.UKJENT);
    }

    @Test
    void bådeFpsoknadOppslagOgNyttEndepunktFeiler() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        var bankkonto = pdlConnection.kontonr();
        assertThat(bankkonto).isEqualTo(Bankkonto.UKJENT);
    }

    private String happyCaseBodyFraNyttEndepunkt() {
        return """
                  {
                      "kontonummer": "8361347234732292",
                      "utenlandskKontoInfo": {
                        "banknavn": "DNB",
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
    }

}
