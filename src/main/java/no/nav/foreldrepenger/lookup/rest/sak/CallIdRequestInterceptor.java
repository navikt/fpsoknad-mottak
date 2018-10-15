package no.nav.foreldrepenger.lookup.rest.sak;

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

    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";

    private UUIDCallIdGenerator callIdGenerator;

    public CallIdRequestInterceptor(UUIDCallIdGenerator callIdGenerator) {
        this.callIdGenerator = callIdGenerator;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String callId = req.getHeaders().getFirst(callIdGenerator.getCallIdKey());
        String consumerId = req.getHeaders().getFirst(NAV_CONSUMER_ID);
        if (consumerId != null) {
            MDC.put(NAV_CONSUMER_ID, consumerId);
        }

        if (callId != null) {
            MDC.put(callIdGenerator.getCallIdKey(), callId);
        }
        else {
            String newCallId = callIdGenerator.create();
            MDC.put(callIdGenerator.getCallIdKey(), newCallId);
            req.getHeaders().set("X-Correlation-ID", newCallId);
        }

        return execution.execute(req, body);
    }

}
