package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.mottak.http.InnsynController.INNSYN;
import static no.nav.foreldrepenger.mottak.http.SøknadController.INNSENDING;
import static no.nav.foreldrepenger.mottak.http.SøknadPreprodController.INNSENDING_PREPROD;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
@Profile({ PREPROD, DEV })
public class IDFilterRegistrationBean extends FilterRegistrationBean<IDToMDCFilter> {

    public IDFilterRegistrationBean(IDToMDCFilter idFiltr) {
        setFilter(idFiltr);
        setUrlPatterns(Lists.newArrayList(INNSENDING + "/*", INNSYN + "/*", INNSENDING_PREPROD + "/*"));
    }
}
