package no.nav.foreldrepenger.mottak.http.filters;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Component
@Profile(PREPROD)
public class FilteringCommonsRequestLoggingFilter extends CommonsRequestLoggingFilter {

    public FilteringCommonsRequestLoggingFilter() {
        setIncludeQueryString(true);
        setIncludePayload(true);
        setIncludeClientInfo(false);
        setMaxPayloadLength(10000);
        setIncludeHeaders(false);
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {

    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return !request.getRequestURI().contains("actuator") && logger.isDebugEnabled();
    }

}