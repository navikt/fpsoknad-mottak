package no.nav.foreldrepenger.mottak.oppslag.sak;

import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpServerErrorException;

@Retryable(include = HttpServerErrorException.class, maxAttempts = 2)
public interface StsClient {

    String oidcToSamlToken(String oidcToken);

    String injectToken(String oidcToken);

}
