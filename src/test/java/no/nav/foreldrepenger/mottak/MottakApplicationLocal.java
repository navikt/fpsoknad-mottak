package no.nav.foreldrepenger.mottak;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.LOCAL;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.foreldrepenger.mottak.config.ClusterAwareSpringProfileResolver;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.oidc.test.support.spring.TokenGeneratorConfiguration;
import no.nav.security.spring.oidc.SpringOIDCRequestContextHolder;

@SpringBootApplication
@EnableCaching
@Import(value = TokenGeneratorConfiguration.class)
@ComponentScan(excludeFilters = { @Filter(type = ASSIGNABLE_TYPE, value = MottakApplication.class) })
public class MottakApplicationLocal {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MottakApplicationLocal.class)
                .profiles(new ClusterAwareSpringProfileResolver().getProfile())
                .main(MottakApplicationLocal.class)
                .run(args);
    }

    @Bean
    @Profile(LOCAL)
    @ConditionalOnMissingBean(SpringOIDCRequestContextHolder.class)
    OIDCRequestContextHolder dummyContextHolderForDev() {
        return new OIDCRequestContextHolder() {

            @Override
            public void setRequestAttribute(String name, Object value) {

            }

            @Override
            public void setOIDCValidationContext(OIDCValidationContext oidcValidationContext) {

            }

            @Override
            public Object getRequestAttribute(String name) {
                return null;
            }

            @Override
            public OIDCValidationContext getOIDCValidationContext() {
                return null;
            }
        };
    }
}
