package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(SpringExtension.class)
class OrganisasjonConnectionTest {
    private static MockWebServer mockWebServer;

    @MockBean
    private PDLConnection pdlConnection;
    private OrganisasjonConnection organisasjonConnection;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(63631);
    }

    @BeforeEach
    void setupConnection() {
        var baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        var webClient = WebClient.builder().baseUrl(baseUrl).build();
        var organisasjonConfig = new OrganisasjonConfig(URI.create(baseUrl), "/v1/organisasjon/{orgnr}", true);
        organisasjonConnection = new OrganisasjonConnection(webClient, pdlConnection, organisasjonConfig);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void returnererNavnSomErSammensettningenAvNavnelinjerSeparertMedKomma() {
        var body = """
                    {
                        "navn": {
                            "navnelinje1": "Sauefabrikk AS",
                            "navnelinje2": "navnelinje2",
                            "navnelinje3": "navnelinje3"
                        }
                    }
                """;
        mockWebServer.enqueue(new MockResponse()
            .setBody(body)
            .addHeader("Content-Type", "application/json"));

        var navn = organisasjonConnection.navn("999999999");
        assertThat(navn).isEqualTo("Sauefabrikk AS, navnelinje2, navnelinje3");
    }

    @Test
    void ingenBodyReturnererOrgnummerSomNavn() {
        mockWebServer.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json"));

        var navn = organisasjonConnection.navn("999999999");
        assertThat(navn).isEqualTo("999999999");
    }

    @Test
    void tomBodyReturnererOrgnummerSomNavn() {
        var body = """
            {}
            """;
        mockWebServer.enqueue(new MockResponse()
            .setBody(body)
            .addHeader("Content-Type", "application/json"));

        var navn = organisasjonConnection.navn("999999999");
        assertThat(navn).isEqualTo("999999999");
    }

    @Test
    void skalBrukeOrgnummerSomDefaultVed4xxFeil() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(404)
            .addHeader("Content-Type", "application/json"));

        var navn = organisasjonConnection.navn("999999999");
        assertThat(navn).isEqualTo("999999999");
    }

    @Test
    void skalBrukeOrgnummerSomDefaultVed5xxFeil() {
        mockWebServer.enqueue(new MockResponse()
            .setBody("ERROR")
            .setResponseCode(500)
            .addHeader("Content-Type", "application/json"));

        var navn = organisasjonConnection.navn("999999999");
        assertThat(navn).isEqualTo("999999999");
    }

}
