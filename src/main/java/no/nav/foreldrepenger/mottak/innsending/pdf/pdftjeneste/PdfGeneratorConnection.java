package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import javax.inject.Inject;
import java.net.URI;

import static no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste.PdfGeneratorConfig.*;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

@Component
public class PdfGeneratorConnection extends AbstractRestConnection implements PingEndpointAware {
    private static final Logger LOG = LoggerFactory.getLogger(PdfGeneratorConnection.class);
    private final PdfGeneratorConfig config;

    @Inject
    private ObjectMapper mapper;

    public PdfGeneratorConnection(RestOperations operations, PdfGeneratorConfig config) {
        super(operations);
        this.config = config;
    }

    public byte[] genererPdf(DokumentBestilling dto) {
        logJSON(dto);
        return postIfEnabled(uri(config.getUri(), ENGANGSSTØNAD), dto);
    }

    private byte[] postIfEnabled(URI uri, DokumentBestilling body) {
        if (config.isEnabled()) {
            return postForObject(uri, body, byte[].class);
        }
        LOG.info("PdfGenerator er ikke aktivert");
        return null;
    }

    private void logJSON(DokumentBestilling dto) {
        try {
            LOG.trace("JSON er {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto));
        } catch (JsonProcessingException e) {

        }
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    @Override
    public URI pingEndpoint() {
        return uri(config.getUri(), config.getPingPath());
    }

    @Override
    public String name() {
        return config.getUri().getHost();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[config=" + config + "]";
    }
}
