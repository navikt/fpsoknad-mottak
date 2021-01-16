package no.nav.foreldrepenger.mottak.http.filters;

import static no.nav.foreldrepenger.mottak.util.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.mottak.util.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.toMDC;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.boot.conditionals.EnvUtil;
import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Component
@Order(LOWEST_PRECEDENCE)
public class HeadersToMDCFilterBean extends GenericFilterBean {
    private static final Logger LOG = LoggerFactory.getLogger(HeadersToMDCFilterBean.class);

    private final CallIdGenerator generator;
    private final String applicationName;
    private final TokenUtil tokenUtil;

    @Inject
    public HeadersToMDCFilterBean(TokenUtil tokenUtil, CallIdGenerator generator,
            @Value("${spring.application.name:fpsoknad-mottak}") String applicationName) {
        this.generator = generator;
        this.applicationName = applicationName;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        putValues(HttpServletRequest.class.cast(request));
        chain.doFilter(request, response);
    }

    private void putValues(HttpServletRequest request) {
        try {
            toMDC(NAV_CONSUMER_ID, request.getHeader(NAV_CONSUMER_ID), applicationName);
            toMDC(NAV_CALL_ID, request.getHeader(NAV_CALL_ID), generator.create());
            if (tokenUtil.erAutentisert() && (EnvUtil.isDev(getEnvironment()))) {
                LOG.warn("Token er {}", tokenUtil.getToken());
            }
        } catch (Exception e) {
            LOG.warn("Noe gikk galt ved setting av MDC-verdier for request {}, MDC-verdier er inkomplette",
                    request.getRequestURI(), e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + ", applicationName=" + applicationName + "]";
    }

}
