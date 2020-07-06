package no.nav.foreldrepenger.mottak.http.interceptors;

import static no.nav.foreldrepenger.mottak.util.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.mottak.util.Constants.NAV_CONSUMER_ID;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
@Order(LOWEST_PRECEDENCE)
public class MDCValuesPropagatingClienHttpRequesInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        propagerFraMDC(request, NAV_CALL_ID, NAV_CONSUMER_ID);
        return execution.execute(request, body);
    }

    private static void propagerFraMDC(HttpRequest request, String... keys) {
        for (String key : keys) {
            String value = MDC.get(key);
            if (value != null) {
                request.getHeaders().add(key, value);
            }
        }
    }
}
