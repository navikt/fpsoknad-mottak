package no.nav.foreldrepenger.mottak.http;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpServerErrorException;

@Retryable(value = { HttpServerErrorException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
public interface RestConnection {
    <T> T getForObject(URI uri, Class<T> responseType);

    <T> T getForObject(URI uri, Class<T> responseType, boolean throwOnNotFound);

    <T> ResponseEntity<T> getForEntity(URI uri, Class<T> responseType, boolean doThrow);

    <T> ResponseEntity<T> getForEntity(URI uri, Class<T> responseType);

}
