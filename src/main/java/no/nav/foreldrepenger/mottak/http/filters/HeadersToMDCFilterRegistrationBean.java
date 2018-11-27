package no.nav.foreldrepenger.mottak.http.filters;

import java.util.Collections;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

@Component
public class HeadersToMDCFilterRegistrationBean extends FilterRegistrationBean<HeadersToMDCFilterBean> {

    public HeadersToMDCFilterRegistrationBean(HeadersToMDCFilterBean headersFilter) {
        setFilter(headersFilter);
        setUrlPatterns(Collections.singletonList("/*"));
        // setUrlPatterns(urlPatternsFor(INNSENDING, INNSYN));
    }
}
