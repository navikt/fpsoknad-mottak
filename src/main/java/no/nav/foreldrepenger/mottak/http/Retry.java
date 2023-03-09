package no.nav.foreldrepenger.mottak.http;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Target({ TYPE, METHOD })
@Retryable(
    exclude = {
        WebClientResponseException.NotFound.class,
        WebClientResponseException.UnsupportedMediaType.class,
        WebClientResponseException.UnprocessableEntity.class,
        WebClientResponseException.BadRequest.class,
        WebClientResponseException.Forbidden.class,
        WebClientResponseException.Unauthorized.class
    },
    maxAttemptsExpression = "#{${rest.retry.attempts:3}}",
    backoff = @Backoff(delayExpression = "#{${rest.retry.delay:500}}")
)
public @interface Retry {

}

