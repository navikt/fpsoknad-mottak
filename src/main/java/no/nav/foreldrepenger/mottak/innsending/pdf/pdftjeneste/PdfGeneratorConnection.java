package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.http.PingEndpointAware;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;

@Component
public class PdfGeneratorConnection extends AbstractRestConnection implements PingEndpointAware {
    private static final Logger LOG = LoggerFactory.getLogger(PdfGeneratorConnection.class);
    private final PdfGeneratorConfig cfg;

    public PdfGeneratorConnection(RestOperations operations, PdfGeneratorConfig cfg) {
        super(operations);
        this.cfg = cfg;
    }

    byte[] genererPdf(DokumentBestilling dto) {
        if (cfg.isEnabled()) {
            return postForObject(cfg.pdfUri(), dto, byte[].class);
        }
        LOG.info("PdfGenerator er ikke aktivert");
        return new byte[0];
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    @Override
    public URI pingEndpoint() {
        return cfg.pingUri();
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
