package no.nav.foreldrepenger.mottak.oppslag.sak;

import java.util.List;

import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpServerErrorException;

import no.nav.foreldrepenger.mottak.domain.AktørId;

@Retryable(include = HttpServerErrorException.class, maxAttempts = 2)
public interface SakClient {
    List<Sak> sakerFor(AktørId aktor, String tema);
}
