package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static no.nav.foreldrepenger.mottak.domain.Kvittering.ikkeSendt;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_SENDFEIL;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;

@Component
public class FordelConnection extends AbstractRestConnection implements PingEndpointAware {

    private static final Logger LOG = LoggerFactory.getLogger(FordelConnection.class);

    private final FordelConfig config;
    private final ResponseHandler responseHandler;
    private final MeterRegistry registry;

    public FordelConnection(RestOperations restOperations, FordelConfig config,
            ResponseHandler responseHandler, MeterRegistry registry) {
        super(restOperations);
        this.config = config;
        this.responseHandler = responseHandler;
        this.registry = registry;
    }

    public Kvittering send(Konvolutt konvolutt, BrukerRolle rolle) {
        if (isEnabled()) {
            return doSend(konvolutt, rolle);
        }
        LOG.info("Sending av {} er deaktivert, ingenting å sende", konvolutt.getType());
        return ikkeSendt(konvolutt.PDFHovedDokument());
    }

    private Kvittering doSend(Konvolutt konvolutt, BrukerRolle rolle) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            LOG.info("Sender {} til {}", name(konvolutt.getType()), name().toLowerCase());
            var kvittering = responseHandler
                    .handle(postForEntity(uri(config.getUri(), config.getBasePath()), konvolutt.getPayload(),
                            FordelKvittering.class));
            stopWatch.stop();
            kvittering.setPdf(konvolutt.PDFHovedDokument());
            LOG.info("Sendte {} til {}, fikk kvittering {}", name(konvolutt.getType()), name().toLowerCase(),
                    kvittering);
            konvolutt.getType().count();
            timer(konvolutt.getType(), rolle, kvittering.getLeveranseStatus()).record(stopWatch.getTime(),
                    MILLISECONDS);
            return kvittering;
        } catch (Exception e) {
            FP_SENDFEIL.increment();
            throw e;
        }
    }

    private Timer timer(SøknadType type, BrukerRolle rolle, LeveranseStatus leveranseStatus) {
        return Timer.builder("application.send")
                .tags("rolle", rolleFra(rolle), "status", leveranseStatus.name(), "type", type.name(), "fagsaktype",
                        type.fagsakType().name())
                .publishPercentiles(0.5, 0.95)
                .publishPercentileHistogram()
                .minimumExpectedValue(Duration.ofSeconds(2))
                .maximumExpectedValue(Duration.ofSeconds(20)).register(registry);
    }

    private static String rolleFra(BrukerRolle rolle) {
        return Optional.ofNullable(rolle)
                .map(BrukerRolle::name)
                .orElse("N/A");
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    @Override
    public URI pingEndpoint() {
        return uri(config.getUri(), config.getPingPath());
    }

    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public String name() {
        return "fpfordel";
    }

    private static String name(SøknadType type) {
        return type.name().toLowerCase();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + ", responseHandler=" + responseHandler + "]";
    }
}
