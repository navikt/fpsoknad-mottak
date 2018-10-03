package no.nav.foreldrepenger.mottak.http.filters;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;

@Component
@Order(1)
public class HeadersToMDCFilterBean extends GenericFilterBean {

    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String NAV_CALL_ID = "Nav-CallId";

    private final CallIdGenerator generator;
    private final String applicationName;

    @Inject
    public HeadersToMDCFilterBean(CallIdGenerator generator,
            @Value("${spring.application.name}") String applicationName) {
        this.generator = generator;
        this.applicationName = applicationName;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        propagateOrCreate(request);
        chain.doFilter(request, response);
    }

    private void propagateOrCreate(ServletRequest request) {
        HttpServletRequest req = HttpServletRequest.class.cast(request);
        propagateOrCreate(NAV_CONSUMER_ID, req, applicationName);
        propagateOrCreate(NAV_CALL_ID, req, generator.create());
    }

    private static void propagateOrCreate(String key, HttpServletRequest req, String defaultValue) {
        MDC.put(key, Optional.ofNullable(req.getHeader(key)).orElse(defaultValue));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + ", applicationName=" + applicationName + "]";
    }

}
