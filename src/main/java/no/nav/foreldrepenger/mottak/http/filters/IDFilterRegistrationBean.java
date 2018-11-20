package no.nav.foreldrepenger.mottak.http.filters;

import static no.nav.foreldrepenger.mottak.http.controllers.InnsynController.INNSYN;
import static no.nav.foreldrepenger.mottak.http.controllers.SøknadController.INNSENDING;
import static no.nav.foreldrepenger.mottak.http.filters.FilterRegistrationUtil.urlPatternsFor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

@Component
public class IDFilterRegistrationBean extends FilterRegistrationBean<IDToMDCFilterBean> {

    public IDFilterRegistrationBean(IDToMDCFilterBean idFilter) {
        setFilter(idFilter);
        setUrlPatterns(urlPatternsFor(INNSENDING, INNSYN));
    }
}
