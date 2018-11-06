package no.nav.foreldrepenger.lookup.rest.filters;

import static no.nav.foreldrepenger.lookup.Constants.NAV_AKTØR_ID;
import static no.nav.foreldrepenger.lookup.Constants.NAV_USER_ID;
import static no.nav.foreldrepenger.lookup.EnvUtil.isDevOrPreprod;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.lookup.TokenHandler;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;

@Order(HIGHEST_PRECEDENCE)
@Component
public class IDToMDCFilterBean extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(IDToMDCFilterBean.class);

    private final AktorIdClient aktørIdClient;
    private final TokenHandler tokenHandler;

    public IDToMDCFilterBean(TokenHandler tokenHandler, AktorIdClient aktørIdClient) {
        this.tokenHandler = tokenHandler;
        this.aktørIdClient = aktørIdClient;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (tokenHandler.erAutentisert()) {
            copyHeadersToMDC();
        }
        chain.doFilter(req, res);
    }

    private void copyHeadersToMDC() {
        try {
            if (isDevOrPreprod(getEnvironment())) {
                MDC.put(NAV_USER_ID, tokenHandler.getSubject().getFnr());
            }
            MDC.put(NAV_AKTØR_ID, aktørIdClient.aktorIdForFnr(tokenHandler.getSubject()).getAktør());
        } catch (Exception e) {
            LOG.warn("Noe gikk feil, MDC-verdier er inkomplette", e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktørIdClient=" + aktørIdClient + ", tokenHandler=" + tokenHandler + "]";
    }
}
