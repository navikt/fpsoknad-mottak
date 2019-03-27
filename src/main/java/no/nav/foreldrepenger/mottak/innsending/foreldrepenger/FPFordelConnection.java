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
import io.micrometer.core.instrument.Timer.Builder;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
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

    public Kvittering send(SøknadType type, BrukerRolle rolle, FPFordelKonvolutt konvolutt) {
        if (isEnabled()) {
            return doSend(type, rolle, konvolutt);
        }
        LOG.info("Sending av {} er deaktivert, ingenting å sende", type);
        return ikkeSendt(konvolutt.PDFHovedDokument());
    }

    private Kvittering doSend(SøknadType type, BrukerRolle rolle, FPFordelKonvolutt konvolutt) {
        try {

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
            timer(type, rolle, kvittering.getLeveranseStatus()).record(stopWatch.getTime(), MILLISECONDS);
            kvittering.setPdf(konvolutt.PDFHovedDokument());
            return kvittering;
        } catch (Exception e) {
            FP_SENDFEIL.increment();
            throw e;
        }
    }

    private Timer timer(SøknadType type, BrukerRolle rolle, LeveranseStatus leveranseStatus) {
        Builder builder = Timer.builder("application.send")
                .tags("status", leveranseStatus.name(), "type", type.name(), "fagsaktype", type.fagsakType().name())
                .publishPercentiles(0.5, 0.95)
                .publishPercentileHistogram()
                .minimumExpectedValue(Duration.ofSeconds(2))
                .maximumExpectedValue(Duration.ofSeconds(20));
        /*
         * if (rolle != null) { builder.tag("rolle", rolle.name()); }
         */
        return builder.register(registry);
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
