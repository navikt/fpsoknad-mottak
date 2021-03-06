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
    private final PdfGeneratorConfig config;

    public PdfGeneratorConnection(RestOperations operations, PdfGeneratorConfig config) {
        super(operations);
        this.config = config;
    }

    byte[] genererPdf(DokumentBestilling dto) {
        if (config.isEnabled()) {
            return postForObject(config.pdfUri(), dto, byte[].class);
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
        return config.pingUri();
    }

    @Override
    public String name() {
        return config.name();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[config=" + config + "]";
    }
}
