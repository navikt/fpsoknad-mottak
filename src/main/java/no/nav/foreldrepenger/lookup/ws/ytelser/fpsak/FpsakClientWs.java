package no.nav.foreldrepenger.lookup.ws.ytelser.fpsak;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import no.nav.foreldrepenger.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.binding.FinnSakListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.binding.ForeldrepengesakV1;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.meldinger.FinnSakListeRequest;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.meldinger.FinnSakListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class FpsakClientWs implements FpsakClient {
    private static final Logger LOG = LoggerFactory.getLogger(FpsakClientWs.class);

    private final ForeldrepengesakV1 fpsakV1;
    private final ForeldrepengesakV1 healthIndicator;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.fpsak");
    private static final Timer timer = Metrics.timer("lookup.fpsak");

    public FpsakClientWs(ForeldrepengesakV1 fpsakV1, ForeldrepengesakV1 healthIndicator) {
        this.fpsakV1 = Objects.requireNonNull(fpsakV1);
        this.healthIndicator = Objects.requireNonNull(healthIndicator);
    }

    @Override
    public void ping() {
        try {
            LOG.info("Pinger FPsak");
            healthIndicator.ping();
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw ex;
        }
    }

    @Override
    public List<Ytelse> casesFor(AktorId aktor) {
        FinnSakListeRequest req = new FinnSakListeRequest();
        Aktoer a = new Aktoer();
        a.setAktoerId(aktor.getAktør());
        req.setSakspart(a);
        long start = System.currentTimeMillis();
        try {
            FinnSakListeResponse res = fpsakV1.finnSakListe(req);
            return res.getSakListe().stream().map(SakMapper::map).collect(toList());
        } catch (FinnSakListeSikkerhetsbegrensning ex) {
            throw new ForbiddenException(ex);
        } catch (Exception ex) {
            LOG.warn("Error while reading from Fpsak", ex);
            ERROR_COUNTER.increment();
            throw new RuntimeException("Error while reading from Fpsak", ex);
        } finally {
            long end = System.currentTimeMillis();
            timer.record(end - start, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public String toString() {
        return "FpsakKlient{" + "fpsakV1=" + fpsakV1 + '}';
    }
}
