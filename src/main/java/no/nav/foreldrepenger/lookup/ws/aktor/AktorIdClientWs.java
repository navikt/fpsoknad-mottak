package no.nav.foreldrepenger.lookup.ws.aktor;

import java.util.Objects;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.errorhandling.NotFoundException;
import no.nav.foreldrepenger.errorhandling.RemoteUnavailableException;
import no.nav.foreldrepenger.errorhandling.TokenExpiredException;
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
    @Cacheable(cacheNames = "aktoer")
    @Retryable(value = { SOAPFaultException.class }, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public AktorId aktorIdForFnr(Fødselsnummer fnr) {
        try {
            return new AktorId(aktoerV2.hentAktoerIdForIdent(request(fnr)).getAktoerId());
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            LOG.warn("Henting av aktørid har feilet", e);
            throw new NotFoundException(e);
        } catch (SOAPFaultException e) {
            ERROR_COUNTER_AKTOR.increment();
            if (tokenHandler.isExpired()) {
                throw new TokenExpiredException(tokenHandler.getExp(), e);
            }
            throw e;
        } catch (WebServiceException e) {
            ERROR_COUNTER_AKTOR.increment();
            throw new RemoteUnavailableException(e);
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
            ERROR_COUNTER_FNR.increment();
            if (tokenHandler.isExpired()) {
                throw new TokenExpiredException(tokenHandler.getExp(), e);
            }
            throw e;
        } catch (WebServiceException e) {
            ERROR_COUNTER_FNR.increment();
            throw new RemoteUnavailableException(e);
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
