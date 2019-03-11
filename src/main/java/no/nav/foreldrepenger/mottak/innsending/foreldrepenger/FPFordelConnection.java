package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.Kvittering.IKKE_SENDT;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.HOVEDDOKUMENT;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_SENDFEIL;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;
import static org.springframework.http.MediaType.APPLICATION_PDF;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;

@Component
public class FPFordelConnection extends AbstractRestConnection implements PingEndpointAware {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelConnection.class);

    private final FPFordelConfig config;
    private final FPFordelResponseHandler responseHandler;

    public FPFordelConnection(RestOperations restOperations, FPFordelConfig config,
            FPFordelResponseHandler responseHandler) {
        super(restOperations);
        this.config = config;
        this.responseHandler = responseHandler;
    }

    public Kvittering send(SøknadType type, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        if (isEnabled()) {
            return doSend(type, payload);
        }
        LOG.info("Sending av {} er deaktivert, ingenting å sende", type);
        if (payload.getBody() != null) {
            IKKE_SENDT.setPdf(pdfFra(payload.getBody()));
        }
        return IKKE_SENDT;
    }

    private Kvittering doSend(SøknadType type, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        try {
            LOG.info("Sender {} til {}", name(type), name().toLowerCase());
            Kvittering kvittering = responseHandler.handle(
                    postForEntity(uri(config.getUri(), config.getBasePath()), payload, FPFordelKvittering.class));
            LOG.info("Sendte {} til {}, fikk kvittering {}", name(type), name().toLowerCase(),
                    kvittering);
            type.count();
            kvittering.setPdf(pdfFra(payload.getBody()));
            return kvittering;
        } catch (Exception e) {
            FP_SENDFEIL.increment();
            throw e;
        }
    }

    @Override
    public String ping() {
        URI pingEndpoint = pingEndpoint();
        LOG.info("Pinger {}", pingEndpoint);
        return ping(pingEndpoint);
    }

    @Override
    public URI pingEndpoint() {
        return uri(config.getUri(), config.getPingPath());
    }

    private boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public String name() {
        return "FPFORDEL";
    }

    private static byte[] pdfFra(MultiValueMap<String, HttpEntity<?>> body) {
        byte[] bytes = safeStream(body.get(HOVEDDOKUMENT))
                .filter(e -> APPLICATION_PDF.equals(e.getHeaders().getContentType()))
                .filter(HttpEntity::hasBody)
                .map(HttpEntity::getBody)
                .map(byte[].class::cast)
                .findFirst()
                .orElse(null);
        if (bytes != null) {
            LOG.info("Returnerer PDF med størrelse {} i kvittering", bytes.length);
        }
        else {
            LOG.info("Returnerer ingen PDF i kvittering");
        }
        return bytes;
    }

    private static String name(SøknadType type) {
        return type.name().toLowerCase();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + ", responseHandler=" + responseHandler + "]";
    }
}
