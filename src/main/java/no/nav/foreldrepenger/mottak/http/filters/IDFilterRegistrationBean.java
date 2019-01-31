package no.nav.foreldrepenger.mottak.http.filters;

import static no.nav.foreldrepenger.mottak.http.filters.FilterRegistrationUtil.urlPatternsFor;
import static no.nav.foreldrepenger.mottak.innsending.SøknadController.INNSENDING;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynController.INNSYN;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

//@Component
public class IDFilterRegistrationBean extends FilterRegistrationBean<IDToMDCFilterBean> {
    private static final Logger LOG = LoggerFactory.getLogger(HeadersToMDCFilterRegistrationBean.class);

    public IDFilterRegistrationBean(IDToMDCFilterBean idFilter) {
        setFilter(idFilter);
        setUrlPatterns(urlPatternsFor(INNSENDING, INNSYN));
        LOG.info("Registrert filter {}", this);
    }
}
