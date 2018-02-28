package no.nav.foreldrepenger.mottak.http;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@Order(1)
public class CallIdFilter extends GenericFilterBean {

    private static final Logger LOG = getLogger(CallIdFilter.class);

    private final String key;

    private final CallIdGenerator generator;

    @Inject
    public CallIdFilter(@Value("${callid.key:X-Nav-CallId}") String key, CallIdGenerator generator) {
        this.key = key;
        this.generator = generator;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        getOrCreateCallId(request);
        chain.doFilter(request, response);
    }

    private void getOrCreateCallId(ServletRequest req) {
        String callId = HttpServletRequest.class.cast(req).getHeader(key);
        if (callId != null) {
            LOG.info("CallId is already set to {} in request, now setting in MDC under key {}", callId, key);
            MDC.put(key, callId);
        } else {
            MDC.put(key, generator.getOrCreate());
            LOG.info("Callid was not set in request, now set in MDC to {}", MDC.get(key));
        }
        LOG.info("MDC values now {}", MDC.getCopyOfContextMap());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [key=" + key + ", generator=" + generator + "]";
    }
}
