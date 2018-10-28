package no.nav.foreldrepenger.mottak.http.filters;

import static no.nav.foreldrepenger.mottak.http.Constants.NAV_AKTØR_ID;
import static no.nav.foreldrepenger.mottak.http.Constants.NAV_USER_ID;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.TokenHandler;

@Order(HIGHEST_PRECEDENCE)
@Profile({ PREPROD, DEV })
@Component
public class IDToMDCFilterBean extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(IDToMDCFilterBean.class);

    private final Oppslag oppslag;
    private final TokenHandler handler;

    public IDToMDCFilterBean(TokenHandler handler, Oppslag oppslag) {
        this.handler = handler;
        this.oppslag = oppslag;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (handler.erAutentisert()) {
            tilMDC();
        }
        chain.doFilter(req, res);
    }

    private void tilMDC() {
        try {
            MDC.put(NAV_USER_ID, handler.autentisertBruker().getFnr());
            MDC.put(NAV_AKTØR_ID, oppslag.getAktørId().getId());
        } catch (Exception e) {
            LOG.warn("Noe gikk feil", e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", handler=" + handler + "]";
    }
}
