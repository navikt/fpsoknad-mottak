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

import no.nav.foreldrepenger.mottak.domain.CorrelationIdGenerator;

@Component
@Order(1)
public class CorrelationIdFilter extends GenericFilterBean {

    private static final Logger LOG = getLogger(CorrelationIdFilter.class);

    private final CorrelationIdGenerator generator;

    @Inject
    public CorrelationIdFilter(CorrelationIdGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        getOrCreateCorrelationId(request);
        chain.doFilter(request, response);
    }

    private void getOrCreateCorrelationId(ServletRequest req) {
        String key = generator.getKey();
        String correlationId = HttpServletRequest.class.cast(req).getHeader(key);
        if (correlationId != null) {
            LOG.trace("{} is set in request to {}", key, correlationId);
            MDC.put(key, correlationId);
        } else {
            MDC.put(key, generator.create());
            LOG.trace("{} was not set in request, now set in MDC to {}", key, MDC.get(key));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + "]";
    }
}
