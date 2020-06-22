package no.nav.foreldrepenger.mottak.http;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpServerErrorException.BadGateway;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Retryable(include = {
        ResourceAccessException.class,
        WebClientResponseException.class,
        BadGateway.class }, exclude = {
                HttpClientErrorException.NotFound.class,
                WebClientResponseException.NotFound.class,
                WebClientResponseException.Forbidden.class,
                InternalServerError.class,
                Forbidden.class }, maxAttemptsExpression = "#{${rest.retry.attempts:3}}", backoff = @Backoff(delayExpression = "#{${rest.retry.delay:1000}}"))

public interface RetryAware {

}
