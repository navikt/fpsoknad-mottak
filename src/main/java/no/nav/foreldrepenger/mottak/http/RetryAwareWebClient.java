package no.nav.foreldrepenger.mottak.http;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

public final class RetryAwareWebClient {
    private static final Logger LOG = LoggerFactory.getLogger(RetryAwareWebClient.class);

    private RetryAwareWebClient() {
    }

    public static RetryBackoffSpec retrySpec(String name) {
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
            .filter(ex -> ex instanceof WebClientResponseException webClientResponseException && webClientResponseException.getStatusCode().is5xxServerError())
            .doBeforeRetry(retrySignal -> LOG.info("Kall mot {} kastet exception {} for {}. gang", name, retrySignal.failure(), retrySignal.totalRetriesInARow() + 1));
    }
}
