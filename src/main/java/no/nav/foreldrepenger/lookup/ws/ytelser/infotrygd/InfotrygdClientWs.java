package no.nav.foreldrepenger.lookup.ws.ytelser.infotrygd;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import no.nav.foreldrepenger.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.errorhandling.NotFoundException;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.time.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.FinnSakListePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.FinnSakListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.InfotrygdSakV1;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.meldinger.FinnSakListeRequest;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.meldinger.FinnSakListeResponse;

public class InfotrygdClientWs implements InfotrygdClient {
    private static final Logger log = LoggerFactory.getLogger(InfotrygdClientWs.class);

    private final InfotrygdSakV1 infotrygd;
    private final InfotrygdSakV1 healthIndicator;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.infotrygd");
    private static final Logger LOG = LoggerFactory.getLogger(InfotrygdClientWs.class);

    @Inject
    public InfotrygdClientWs(InfotrygdSakV1 infotrygd, InfotrygdSakV1 healthIdicator) {
        this.infotrygd = infotrygd;
        this.healthIndicator = healthIdicator;
    }

    public void ping() {
        try {
            LOG.info("Pinger Infotrygd");
            healthIndicator.ping();
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw ex;
        }
    }

    public List<Ytelse> casesFor(Fødselsnummer fnr, LocalDate from, LocalDate to) {
        FinnSakListeRequest req = new FinnSakListeRequest();
        Periode periode = new Periode();
        periode.setFom(DateUtil.toXMLGregorianCalendar(from));
        periode.setTom(DateUtil.toXMLGregorianCalendar(to));
        req.setPeriode(periode);
        req.setPersonident(fnr.getFnr());
        try {
            FinnSakListeResponse res = infotrygd.finnSakListe(req);
            return res.getSakListe().stream().map(InfotrygdsakMapper::map).collect(toList());
        } catch (FinnSakListePersonIkkeFunnet ex) {
            throw new NotFoundException(ex);
        } catch (FinnSakListeSikkerhetsbegrensning ex) {
            log.warn("Security error from Infotrygd", ex);
            throw new ForbiddenException(ex);
        } catch (Exception ex) {
            log.warn("Error while reading from Infotrygd", ex);
            ERROR_COUNTER.increment();
            throw new RuntimeException("Error while reading from Infotrygd", ex);
        }
    }

}
