package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.Kvittering.IKKE_SENDT;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_SENDFEIL;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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

    public Kvittering send(SøknadType type, FPFordelKonvolutt konvolutt) {
        if (isEnabled()) {
            return doSend(type, konvolutt);
        }
        LOG.info("Sending av {} er deaktivert, ingenting å sende", type);
        IKKE_SENDT.setPdf(konvolutt.PDFHovedDokument());
        return IKKE_SENDT;
    }

    private Kvittering doSend(SøknadType type, FPFordelKonvolutt konvolutt) {
        try {
            LOG.info("Sender {} til {}", name(type), name().toLowerCase());
            Kvittering kvittering = responseHandler.handle(
                    postForEntity(uri(config.getUri(), config.getBasePath()), konvolutt.getPayload(),
                            FPFordelKvittering.class));
            LOG.info("Sendte {} til {}, fikk kvittering {}", name(type), name().toLowerCase(),
                    kvittering);
            type.count();
            kvittering.setPdf(konvolutt.PDFHovedDokument());
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

    private static String name(SøknadType type) {
        return type.name().toLowerCase();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + ", responseHandler=" + responseHandler + "]";
    }
}
