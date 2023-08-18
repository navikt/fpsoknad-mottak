package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class PdfGeneratorConnectionTest {

    private static MockWebServer mockWebServer;
    private static PdfGeneratorConnection pdfGeneratorConnection;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        var baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        var webClient = WebClient.create();
        var pdfGeneratorConfig = new PdfGeneratorConfig(URI.create(baseUrl), "/api/v1/genpdf/soknad-v2/soknad", true);
        pdfGeneratorConnection = new PdfGeneratorConnection(webClient, pdfGeneratorConfig);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void verifiserAtKlientKlarerÅReturnereByteArray() {
        var ettEllerAnnet = """
            This license is copied below, and is also available with a FAQ at:
            http://scripts.sil.org/OFL

            -----------------------------------------------------------
            SIL OPEN FONT LICENSE Version 1.1 - 26 February 2007
            -----------------------------------------------------------

            PREAMBLE
            """;
        mockWebServer.enqueue(new MockResponse()
            .setBody(ettEllerAnnet)
            .addHeader("Content-Type", "application/json"));

        var result = pdfGeneratorConnection.genererPdf(new DokumentBestilling("test", null, null, null));
        assertThat(result).isEqualTo(ettEllerAnnet.getBytes());

    }

}
