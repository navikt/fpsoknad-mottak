package no.nav.foreldrepenger.mottak.http.filters;

import static no.nav.foreldrepenger.mottak.http.filters.FilterRegistrationUtil.urlPatternsFor;
import static no.nav.foreldrepenger.mottak.innsending.SøknadController.INNSENDING;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynController.INNSYN;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

@Component
public class HeadersToMDCFilterRegistrationBean extends FilterRegistrationBean<HeadersToMDCFilterBean> {
    private static final Logger LOG = LoggerFactory.getLogger(HeadersToMDCFilterRegistrationBean.class);

    public HeadersToMDCFilterRegistrationBean(HeadersToMDCFilterBean headersFilter) {
        setFilter(headersFilter);
        setUrlPatterns(urlPatternsFor(INNSENDING, INNSYN));
        LOG.info("Registrert filter {}", this);
    }
}
