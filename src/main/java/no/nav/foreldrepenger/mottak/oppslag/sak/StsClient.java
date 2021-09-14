package no.nav.foreldrepenger.mottak.oppslag.sak;

import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpServerErrorException;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.http.RetryAware;

@Retryable(include = HttpServerErrorException.class, maxAttempts = 2)
public interface StsClient extends RetryAware {

    String oidcToSamlToken(String oidcToken, Fødselsnummer fødselsnummer);

    String injectToken(String oidcToken);

}
