package no.nav.foreldrepenger.mottak.http.filters;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.FnrExtractor;

@Order(HIGHEST_PRECEDENCE)
@Profile({ PREPROD, DEV })
@Component
public class IDToMDCFilterBean extends GenericFilterBean {

    private static final String USER_ID = "Nav-User-Id";
    private static final String AKTØR_ID = "Nav-Aktør-Id";

    private final Oppslag oppslag;
    private final FnrExtractor extractor;

    public IDToMDCFilterBean(FnrExtractor extractor, Oppslag oppslag) {
        this.extractor = extractor;
        this.oppslag = oppslag;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        MDC.put(USER_ID, extractor.fnrFromToken());
        MDC.put(AKTØR_ID, oppslag.getAktørId().getId());
        chain.doFilter(req, res);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", extractor=" + extractor + "]";
    }
}
