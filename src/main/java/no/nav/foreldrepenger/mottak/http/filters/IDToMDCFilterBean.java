package no.nav.foreldrepenger.mottak.http.filters;

import static no.nav.foreldrepenger.mottak.http.Constants.NAV_AKTØR_ID;
import static no.nav.foreldrepenger.mottak.http.Constants.NAV_USER_ID;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.isDevOrPreprod;
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

import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.TokenHelper;

@Order(HIGHEST_PRECEDENCE)
@Component
public class IDToMDCFilterBean extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(IDToMDCFilterBean.class);

    private final Oppslag oppslag;
    private final TokenHelper tokenHelper;

    public IDToMDCFilterBean(TokenHelper tokenHelper, Oppslag oppslag) {
        this.tokenHelper = tokenHelper;
        this.oppslag = oppslag;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (tokenHelper.erAutentisert()) {
            copyHeadersToMDC();
        }
        chain.doFilter(req, res);
    }

    private void copyHeadersToMDC() {
        try {
            if (isDevOrPreprod(getEnvironment())) {
                MDC.put(NAV_USER_ID, tokenHelper.autentisertBruker().getFnr());
            }
            MDC.put(NAV_AKTØR_ID, oppslag.getAktørId().getId());
        } catch (Exception e) {
            LOG.trace("Noe gikk feil ved oppslag av aktørid, MDC-verdier er inkomplette", e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", tokenHelper=" + tokenHelper + "]";
    }
}
