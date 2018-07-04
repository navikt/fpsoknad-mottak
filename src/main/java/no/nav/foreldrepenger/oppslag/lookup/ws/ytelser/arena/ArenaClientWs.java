package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.arena;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.oppslag.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.time.DateUtil;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.binding.HentYtelseskontraktListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.binding.YtelseskontraktV3;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Periode;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.HentYtelseskontraktListeRequest;

public class ArenaClientWs implements ArenaClient {
    private static final Logger LOG = LoggerFactory.getLogger(ArenaClientWs.class);

    private final YtelseskontraktV3 ytelser;
    private final YtelseskontraktV3 healthIndicator;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.arena");

    @Inject
    public ArenaClientWs(YtelseskontraktV3 ytelser, YtelseskontraktV3 healthIndicator) {
        this.ytelser = ytelser;
        this.healthIndicator = healthIndicator;
    }

    public void ping() {
        try {
            LOG.info("Pinger Arena");
            healthIndicator.ping();
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw ex;
        }
    }

    public List<Ytelse> ytelser(Fødselsnummer fnr, LocalDate from, LocalDate to) {
        try {
            return ytelser.hentYtelseskontraktListe(request(fnr, from, to)).getYtelseskontraktListe().stream()
                    .map(YtelseskontraktMapper::map)
                    .collect(toList());
        } catch (HentYtelseskontraktListeSikkerhetsbegrensning ex) {
            LOG.warn("Sikkehetsfeil fra Arena", ex);
            throw new ForbiddenException(ex);
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw ex;
        }
    }

    private HentYtelseskontraktListeRequest request(Fødselsnummer fnr, LocalDate from, LocalDate to) {
        HentYtelseskontraktListeRequest req = new HentYtelseskontraktListeRequest();
        Periode periode = new Periode();
        periode.setFom(DateUtil.toXMLGregorianCalendar(from));
        periode.setTom(DateUtil.toXMLGregorianCalendar(to));
        req.setPeriode(periode);
        req.setPersonidentifikator(fnr.getFnr());
        return req;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ytelser=" + ytelser + "]";
    }
}
