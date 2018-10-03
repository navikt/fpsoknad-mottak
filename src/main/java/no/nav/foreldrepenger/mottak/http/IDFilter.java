package no.nav.foreldrepenger.mottak.http;

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

import no.nav.foreldrepenger.mottak.util.FnrExtractor;

@Order(HIGHEST_PRECEDENCE)
@Profile({ PREPROD, DEV })
@Component
public class IDFilter extends GenericFilterBean {

    private static final String USER_ID = "Nav-User-Id";
    private static final String AKTØR_ID = "Nav-Aktør-Id";

    private final Oppslag oppslag;
    private final FnrExtractor extractor;

    public IDFilter(FnrExtractor extractor, Oppslag oppslag) {
        this.extractor = extractor;
        this.oppslag = oppslag;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        putIdFields();
        chain.doFilter(request, response);
    }

    private void putIdFields() {
        MDC.put(USER_ID, extractor.fnrFromToken());
        MDC.put(AKTØR_ID, oppslag.getAktørId().getId());
    }

}
