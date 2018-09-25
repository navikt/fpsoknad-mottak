package no.nav.foreldrepenger.lookup.ws.ytelser.gsak;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.errorhandling.IncompleteRequestException;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import no.nav.tjeneste.virksomhet.sak.v2.SakV2;
import no.nav.tjeneste.virksomhet.sak.v2.WSFinnSakerForBrukerRequest;
import no.nav.tjeneste.virksomhet.sak.v2.WSFinnSakerForBrukerResponse;
import no.nav.tjeneste.virksomhet.sak.v2.WSSikkerhetsbegrensningException;
import no.nav.tjeneste.virksomhet.sak.v2.WSUgyldigInputException;

public class GsakClientWs implements GsakClient {

    private final SakV2 gsak;
    private final SakV2 healthIndicator;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.gsak");
    private static final Logger LOG = LoggerFactory.getLogger(GsakClientWs.class);

    @Inject
    public GsakClientWs(SakV2 gsak, SakV2 healthIdicator) {
        this.gsak = gsak;
        this.healthIndicator = healthIdicator;
    }

    @Override
    public void ping() {
        try {
            LOG.info("Pinger Gsak");
            healthIndicator.ping();
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw ex;
        }
    }

    @Override
    public List<Sak> casesFor(Fødselsnummer fnr) {
        WSFinnSakerForBrukerRequest request = new WSFinnSakerForBrukerRequest();
        request.setBrukerId(fnr.getFnr());
        try {
            WSFinnSakerForBrukerResponse response = gsak.finnSakerForBruker(request);
            LOG.info("Fant {} saker i gsak", response.getSaker().size());
            return response.getSaker().stream().map(GsakMapper::map).collect(toList());
        } catch (WSSikkerhetsbegrensningException ex) {
            ERROR_COUNTER.increment();
            throw new ForbiddenException(ex);
        } catch (WSUgyldigInputException ex) {
            ERROR_COUNTER.increment();
            throw new IncompleteRequestException(ex);
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw new RuntimeException("Error while reading from Gsak", ex);
        }
    }
}
