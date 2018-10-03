package no.nav.foreldrepenger.mottak.http.filters;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.util.MDCUtil;

@Component
@Order(1)
public class CallAndConsumerIdFilter extends GenericFilterBean {

    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";

    private final CallIdGenerator generator;
    private final String applicationName;

    @Inject
    public CallAndConsumerIdFilter(CallIdGenerator generator,
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
        propagateOrCreate(generator.getCallIdKey(), req, generator.create());
    }

    private static void propagateOrCreate(String key, HttpServletRequest req, String defaultValue) {
        MDCUtil.put(key, Optional.ofNullable(req.getHeader(key)).orElse(defaultValue));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + ", applicationName=" + applicationName + "]";
    }

}
