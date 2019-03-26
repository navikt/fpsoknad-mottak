package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static no.nav.foreldrepenger.mottak.domain.Kvittering.ikkeSendt;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_SENDFEIL;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;
import java.time.Duration;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;

@Component
public class FPFordelConnection extends AbstractRestConnection implements PingEndpointAware {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelConnection.class);

    private final FPFordelConfig config;
    private final FPFordelResponseHandler responseHandler;
    private final MeterRegistry registry;

    public FPFordelConnection(RestOperations restOperations, FPFordelConfig config,
            FPFordelResponseHandler responseHandler, MeterRegistry registry) {
        super(restOperations);
        this.config = config;
        this.responseHandler = responseHandler;
        this.registry = registry;
    }

    public Kvittering send(SøknadType type, FPFordelKonvolutt konvolutt) {
        if (isEnabled()) {
            return doSend(type, konvolutt);
        }
        LOG.info("Sending av {} er deaktivert, ingenting å sende", type);
        return ikkeSendt(konvolutt.PDFHovedDokument());
    }

    private Kvittering doSend(SøknadType type, FPFordelKonvolutt konvolutt) {
        try {
            Timer t = timer(type);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            LOG.info("Sender {} til {}", name(type), name().toLowerCase());
            Kvittering kvittering = responseHandler.handle(
                    postForEntity(uri(config.getUri(), config.getBasePath()), konvolutt.getPayload(),
                            FPFordelKvittering.class));
            LOG.info("Sendte {} til {}, fikk kvittering {}", name(type), name().toLowerCase(),
                    kvittering);
            stopWatch.stop();
            type.count();
            t.record(stopWatch.getTime(), MILLISECONDS);
            kvittering.setPdf(konvolutt.PDFHovedDokument());
            return kvittering;
        } catch (Exception e) {
            FP_SENDFEIL.increment();
            throw e;
        }
    }

    private Timer timer(SøknadType type) {
        return Timer.builder("application.send")
                .tags("type", type.name())
                .publishPercentiles(0.5, 0.95)
                .publishPercentileHistogram()
                .sla(Duration.ofMillis(100))
                .minimumExpectedValue(Duration.ofMillis(100))
                .maximumExpectedValue(Duration.ofSeconds(1))
                .register(registry);
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
