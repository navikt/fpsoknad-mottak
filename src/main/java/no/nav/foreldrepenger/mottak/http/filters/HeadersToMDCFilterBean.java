package no.nav.foreldrepenger.mottak.http.filters;

import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.common.util.MDCUtil.toMDC;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.common.util.CallIdGenerator;


/**
 * Brukes ved innkommende requester for å hente ut headere fra request og sette tilsvarende MDC verdier
 *  -   Nav-Consumer-Id
 *  -   callid
 *  -   JTI
 */
@Component
public class HeadersToMDCFilterBean extends GenericFilterBean {
    private static final Logger LOG = LoggerFactory.getLogger(HeadersToMDCFilterBean.class);
    public static final String JTI = "JTI";

    private final CallIdGenerator generator = new CallIdGenerator();
    private final String applicationName;

    public HeadersToMDCFilterBean(@Value("${spring.application.name:fpsoknad-mottak}") String applicationName) {
        this.applicationName = applicationName;
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
            toMDC(JTI, request.getHeader(JTI), "ikke satt");
        } catch (Exception e) {
            LOG.warn("Noe gikk galt ved setting av MDC-verdier for request {}, MDC-verdier er inkomplette", request.getRequestURI(), e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + ", applicationName=" + applicationName + "]";
    }

}
