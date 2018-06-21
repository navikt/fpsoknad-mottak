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

import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;

@Component
@Order(1)
public class CallAndConsumerIdFilter extends GenericFilterBean {

    private static final String NAV_USER_ID = "userId";

    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";

    private static final Logger LOG = getLogger(CallAndConsumerIdFilter.class);

    private final CallIdGenerator generator;

    private final FnrExtractor extractor;

    @Inject
    public CallAndConsumerIdFilter(CallIdGenerator generator, FnrExtractor extractor) {
        this.generator = generator;
        this.extractor = extractor;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        handleMDCvalues(request);
        chain.doFilter(request, response);
    }

    private void handleMDCvalues(ServletRequest request) {
        HttpServletRequest req = HttpServletRequest.class.cast(request);
        propagateOrcreate(NAV_CONSUMER_ID, req, "fpsoknad-mottak");
        propagateOrcreate(generator.getCallIdKey(), req, generator.create());
        MDC.put(NAV_USER_ID, extractor.fnrFromToken());
    }

    private static void propagateOrcreate(String key, HttpServletRequest req, String defaultValue) {
        String value = req.getHeader(key);
        if (value != null) {
            LOG.trace("{} is set in request to {}", key, value);
            MDC.put(key, value);
        }
        else {
            MDC.put(key, defaultValue);
            LOG.trace("{} was not set in request, now set in MDC to {}", key, MDC.get(key));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + "]";
    }
}
