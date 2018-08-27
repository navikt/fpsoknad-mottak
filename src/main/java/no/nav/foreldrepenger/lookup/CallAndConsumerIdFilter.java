package no.nav.foreldrepenger.lookup;

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

@Component
@Order(1)
public class CallAndConsumerIdFilter extends GenericFilterBean {

    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";

    private static final Logger LOG = getLogger(CallAndConsumerIdFilter.class);

    private final UUIDCallIdGenerator generator;

    @Inject
    public CallAndConsumerIdFilter(UUIDCallIdGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        getOrCreateCallId(request);
        chain.doFilter(request, response);
    }

    private void getOrCreateCallId(ServletRequest request) {
        HttpServletRequest req = HttpServletRequest.class.cast(request);
        String callId = req.getHeader(generator.getCallIdKey());
        String consumerId = req.getHeader(NAV_CONSUMER_ID);
        if (consumerId != null) {
            MDC.put(NAV_CONSUMER_ID, consumerId);
        }
        if (callId != null) {
            LOG.trace("{} is already set in request {}", generator.getCallIdKey(), callId);
            MDC.put(generator.getCallIdKey(), callId);
        }
        else {
            MDC.put(generator.getCallIdKey(), generator.create());
            LOG.trace("{} was not set in request, now set in MDC to {}", generator.getCallIdKey(),
                    MDC.get(generator.getCallIdKey()));
        }
    }
}
