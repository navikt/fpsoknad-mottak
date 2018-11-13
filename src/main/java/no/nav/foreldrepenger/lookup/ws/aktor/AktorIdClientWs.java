package no.nav.foreldrepenger.lookup.ws.aktor;

import java.util.Objects;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.errorhandling.NotFoundException;
import no.nav.foreldrepenger.lookup.TokenHandler;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdRequest;

public class AktorIdClientWs implements AktorIdClient {
    private static final Logger LOG = LoggerFactory.getLogger(AktorIdClientWs.class);

    private final AktoerV2 aktoerV2;
    private final AktoerV2 healthIndicator;
    protected final TokenHandler tokenHandler;

    private static final Counter ERROR_COUNTER_AKTOR = Metrics.counter("errors.lookup.aktorid");
    private static final Counter ERROR_COUNTER_FNR = Metrics.counter("errors.lookup.fnr");

    public AktorIdClientWs(AktoerV2 aktoerV2, AktoerV2 healthIndicator, TokenHandler tokenHandler) {
        this.aktoerV2 = Objects.requireNonNull(aktoerV2);
        this.healthIndicator = Objects.requireNonNull(healthIndicator);
        this.tokenHandler = tokenHandler;
    }

    @Override
    @Retryable(value = { SOAPFaultException.class }, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public AktorId aktorIdForFnr(Fødselsnummer fnr) {
        try {
            return new AktorId(aktoerV2.hentAktoerIdForIdent(request(fnr)).getAktoerId());
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            LOG.warn("Henting av aktørid har feilet", e);
            throw new NotFoundException(e);
        } catch (SOAPFaultException e) {
            ERROR_COUNTER_AKTOR.increment();
            LOG.warn("SOAP Fault {}, token utgår {}", e.getFault(), tokenHandler.getExp());
            throw e;
        } catch (Exception e) {
            ERROR_COUNTER_AKTOR.increment();
            throw e;
        }
    }

    @Override
    @Retryable(value = { SOAPFaultException.class }, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public Fødselsnummer fnrForAktørId(AktorId aktørId) {
        try {
            return new Fødselsnummer(aktoerV2.hentIdentForAktoerId(request(aktørId)).getIdent());
        } catch (HentIdentForAktoerIdPersonIkkeFunnet e) {
            LOG.warn("Henting av fnr har feilet", e);
            throw new NotFoundException(e);
        } catch (SOAPFaultException e) {
            ERROR_COUNTER_AKTOR.increment();
            LOG.warn("SOAP Fault {}, token utgår {}", e.getFault(), tokenHandler.getExp());
            throw e;
        } catch (Exception ex) {
            ERROR_COUNTER_FNR.increment();
            throw ex;
        }
    }

    @Override
    public void ping() {
        try {
            LOG.info("Pinger Aktørregisteret");
            healthIndicator.ping();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static HentIdentForAktoerIdRequest request(AktorId aktørId) {
        HentIdentForAktoerIdRequest req = new HentIdentForAktoerIdRequest();
        req.setAktoerId(aktørId.getAktør());
        return req;
    }

    private static HentAktoerIdForIdentRequest request(Fødselsnummer fnr) {
        HentAktoerIdForIdentRequest req = new HentAktoerIdForIdentRequest();
        req.setIdent(fnr.getFnr());
        return req;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktoerV2=" + aktoerV2 + "]";
    }

}
