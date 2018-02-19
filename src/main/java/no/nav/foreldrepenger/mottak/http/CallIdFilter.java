package no.nav.foreldrepenger.mottak.http;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class CallIdFilter extends GenericFilterBean {

    @Inject
    @Value("${callid.key:X-Nav-CallId}")
    private String callIdkey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            MDC.put(callIdkey, callIdFrom(request));

            chain.doFilter(request, response);
        } finally {
            MDC.remove(callIdkey);
        }
    }

    private String callIdFrom(ServletRequest req) {
        return Optional.ofNullable(HttpServletRequest.class.cast(req).getHeader(callIdkey)).orElse(callId());
    }

    private static String callId() {
        return UUID.randomUUID().toString();
    }
}
