package no.nav.foreldrepenger.lookup.rest.sak;

import static no.nav.foreldrepenger.lookup.CallAndConsumerIdFilter.NAV_CONSUMER_ID;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.UUIDCallIdGenerator;

@Component
public class CallIdRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final String X_CORRELATION_ID = "X-Correlation-ID";

    private final UUIDCallIdGenerator callIdGenerator;

    public CallIdRequestInterceptor(UUIDCallIdGenerator callIdGenerator) {
        this.callIdGenerator = callIdGenerator;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        req.getHeaders().set(X_CORRELATION_ID, MDC.get(callIdGenerator.getCallIdKey()));
        req.getHeaders().set(NAV_CONSUMER_ID, MDC.get(NAV_CONSUMER_ID));
        req.getHeaders().set(callIdGenerator.getCallIdKey(), MDC.get(callIdGenerator.getCallIdKey()));
        return execution.execute(req, body);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [callIdGenerator=" + callIdGenerator + "]";
    }
}
