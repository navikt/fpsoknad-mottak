package no.nav.foreldrepenger.mottak.http.filters;

import static no.nav.foreldrepenger.mottak.http.filters.FilterRegistrationUtil.always;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

@Component
public class HeadersToMDCFilterRegistrationBean extends FilterRegistrationBean<HeadersToMDCFilterBean> {

    public HeadersToMDCFilterRegistrationBean(HeadersToMDCFilterBean headersFilter) {
        setFilter(headersFilter);
        setUrlPatterns(always());
    }
}
