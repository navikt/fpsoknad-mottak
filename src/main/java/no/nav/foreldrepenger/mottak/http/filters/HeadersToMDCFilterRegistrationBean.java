package no.nav.foreldrepenger.mottak.http.filters;

import static com.google.common.collect.Lists.newArrayList;
import static no.nav.foreldrepenger.mottak.http.InnsynController.INNSYN;
import static no.nav.foreldrepenger.mottak.http.SøknadController.INNSENDING;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

@Component
public class HeadersToMDCFilterRegistrationBean extends FilterRegistrationBean<HeadersToMDCFilterBean> {

    public HeadersToMDCFilterRegistrationBean(HeadersToMDCFilterBean headersFilter) {
        setFilter(headersFilter);
        setUrlPatterns(newArrayList(INNSENDING + "/*", INNSYN + "/*"));
    }
}
