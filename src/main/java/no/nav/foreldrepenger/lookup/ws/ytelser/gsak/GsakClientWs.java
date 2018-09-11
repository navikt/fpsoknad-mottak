package no.nav.foreldrepenger.lookup.ws.ytelser.gsak;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.errorhandling.IncompleteRequestException;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.tjeneste.virksomhet.sak.v2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class GsakClientWs implements GsakClient {
    private static final Logger log = LoggerFactory.getLogger(GsakClientWs.class);

    private final SakV2 gsak;
    private final SakV2 healthIndicator;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.gsak");
    private static final Logger LOG = LoggerFactory.getLogger(GsakClientWs.class);

    @Inject
    public GsakClientWs(SakV2 gsak, SakV2 healthIdicator) {
        this.gsak = gsak;
        this.healthIndicator = healthIdicator;
    }

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
    public List<Ytelse> casesFor(Fødselsnummer fnr) {
        WSFinnSakerForBrukerRequest request = new WSFinnSakerForBrukerRequest();
        request.setBrukerId(fnr.getFnr());
        try {
            WSFinnSakerForBrukerResponse response = gsak.finnSakerForBruker(request);
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
