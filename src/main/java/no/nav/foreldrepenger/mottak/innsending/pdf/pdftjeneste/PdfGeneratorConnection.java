package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import no.nav.foreldrepenger.mottak.http.Retry;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDF_GENERATOR;

@Component
public class PdfGeneratorConnection extends AbstractWebClientConnection {
    private static final Logger LOG = LoggerFactory.getLogger(PdfGeneratorConnection.class);
    private final PdfGeneratorConfig cfg;

    public PdfGeneratorConnection(@Qualifier(PDF_GENERATOR) WebClient client, PdfGeneratorConfig cfg) {
        super(client, cfg);
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
    public String name() {
        return cfg.name();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[config=" + cfg + "]";
    }
}
