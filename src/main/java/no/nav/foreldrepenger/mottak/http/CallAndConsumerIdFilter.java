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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.mottak.domain.UUIDIdGenerator;

@Component
@Order(1)
public class CallAndConsumerIdFilter extends GenericFilterBean {

    private static final Logger LOG = getLogger(CallAndConsumerIdFilter.class);

    private final UUIDIdGenerator generator;

    @Inject
    public CallAndConsumerIdFilter(UUIDIdGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        getOrCreateCorrelationId(request);
        chain.doFilter(request, response);
    }

    private void getOrCreateCorrelationId(ServletRequest request) {
        String key = generator.getKey();
        HttpServletRequest req = HttpServletRequest.class.cast(request);
        String callId = req.getHeader(key);
        String consumerId = req.getHeader("Nav-Consumer-Id");
        if (consumerId != null) {
            MDC.put("Nav-Consumer-Id", consumerId);
        }
        if (callId != null) {
            LOG.trace("{} is set in request to {}", key, callId);
            MDC.put(key, callId);
        }
        else {
            MDC.put(key, generator.create());
            LOG.trace("{} was not set in request, now set in MDC to {}", key, MDC.get(key));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + "]";
    }
}
