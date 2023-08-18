package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDF_GENERATOR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.Retry;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;
import reactor.core.publisher.Mono;

@Component
public class PdfGeneratorConnection {
    private static final Logger LOG = LoggerFactory.getLogger(PdfGeneratorConnection.class);
    private final WebClient webClient;
    private final PdfGeneratorConfig cfg;

    public PdfGeneratorConnection(@Qualifier(PDF_GENERATOR) WebClient client, PdfGeneratorConfig cfg) {
        this.webClient = client;
        this.cfg = cfg;
    }

    @Retry
    byte[] genererPdf(DokumentBestilling dto) {
        if (cfg.isEnabled()) {
            return webClient.post()
                .uri(cfg.pdfUri())
                .body(Mono.just(dto), DokumentBestilling.class)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
        }
        LOG.info("PdfGenerator er ikke aktivert");
        return new byte[0];
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[config=" + cfg + "]";
    }
}
