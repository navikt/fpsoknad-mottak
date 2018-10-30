package no.nav.foreldrepenger.mottak.http.filters;

import static com.google.common.collect.Lists.newArrayList;
import static no.nav.foreldrepenger.mottak.http.controllers.InnsynController.INNSYN;
import static no.nav.foreldrepenger.mottak.http.controllers.SøknadController.INNSENDING;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

@Component
public class IDFilterRegistrationBean extends FilterRegistrationBean<IDToMDCFilterBean> {

    public IDFilterRegistrationBean(IDToMDCFilterBean idFilter) {
        setFilter(idFilter);
        setUrlPatterns(newArrayList(INNSENDING + "/*", INNSYN + "/*"));
    }
}
