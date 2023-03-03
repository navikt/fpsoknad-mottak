package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.common.domain.AktørId;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;

import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class InnsynConnectionTest {

    private static MockWebServer mockWebServer;
    private static InnsynConnection innsynConnection;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        var baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        var webClient = WebClient.create();
        var innsynConfig = new InnsynConfig("rest/ping", true,  URI.create(baseUrl));
        innsynConnection = new InnsynConnection(webClient, innsynConfig);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void failSafeHentingAvVedtakTilAnnenpartNårSøkerIkkeHarLov() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(FORBIDDEN.code())
            .setBody("Ikke tilgang til å slå opp person!"));

        var annenPartVedtakRequest = new AnnenPartVedtakRequest(AktørId.valueOf("12345678910"),
            AktørId.valueOf("10987654321"),
            null,
            LocalDate.now().minusWeeks(2));
        var uttaksplanAnnenpart = innsynConnection.annenPartVedtak(annenPartVedtakRequest);
        assertThat(uttaksplanAnnenpart).isEmpty();
    }

}
