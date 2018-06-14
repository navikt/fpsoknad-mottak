package no.nav.foreldrepenger.oppslag.lookup.ws.medl;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.oppslag.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.oppslag.errorhandling.NotFoundException;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fodselsnummer;
import no.nav.tjeneste.virksomhet.medlemskap.v2.MedlemskapV2;
import no.nav.tjeneste.virksomhet.medlemskap.v2.PersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.medlemskap.v2.Sikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.Foedselsnummer;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.Personidentifikator;
import no.nav.tjeneste.virksomhet.medlemskap.v2.meldinger.HentPeriodeListeRequest;

@Component
public class MedlClientWs implements MedlClient {
    private static final Logger LOG = LoggerFactory.getLogger(MedlClientWs.class);

    private final MedlemskapV2 medlemskapV2;
    private final MedlemskapV2 healthIndicator;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.medl");

    @Inject
    public MedlClientWs(@Qualifier("medlemskapV2") MedlemskapV2 medlemskapV2,
            @Qualifier("healthIndicator") MedlemskapV2 healthIndicator) {
        this.medlemskapV2 = medlemskapV2;
        this.healthIndicator = healthIndicator;
    }

    public void ping() {
        try {
            LOG.info("Pinger MEDL");
            healthIndicator.ping();
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw ex;
        }
    }

    public List<MedlPeriode> medlInfo(Fodselsnummer fnr) {
        HentPeriodeListeRequest req = new HentPeriodeListeRequest();
        Personidentifikator ident = new Foedselsnummer();
        ident.setValue(fnr.getFnr());
        req.setIdent(ident);
        try {
            return medlemskapV2.hentPeriodeListe(req).getPeriodeListe().stream()
                    .map(MedlemsperiodeMapper::map)
                    .collect(toList());
        } catch (PersonIkkeFunnet ex) {
            throw new NotFoundException(ex);
        } catch (Sikkerhetsbegrensning ex) {
            LOG.warn("Sikkerhetsfeil fra MEDL", ex);
            throw new ForbiddenException(ex);
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw new RuntimeException(ex);
        }
    }

}
