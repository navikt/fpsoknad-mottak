package no.nav.foreldrepenger.mottak;

import static no.nav.foreldrepenger.boot.conditionals.Cluster.profiler;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import java.util.Map;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnLocal;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.test.spring.TokenGeneratorConfiguration;

@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan
@Import(value = TokenGeneratorConfiguration.class)
@ComponentScan(excludeFilters = { @Filter(type = ASSIGNABLE_TYPE, value = MottakApplication.class) })
public class MottakApplicationLocal {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MottakApplicationLocal.class)
                .profiles(profiler())
                .main(MottakApplicationLocal.class)
                .run(args);
    }

    @Bean
    @ConditionalOnLocal
    TokenValidationContextHolder dummyContextHolderForDev() {
        return new TokenValidationContextHolder() {

            @Override
            public TokenValidationContext getTokenValidationContext() {
                return new TokenValidationContext(Map.of());
            }

            @Override
            public void setTokenValidationContext(TokenValidationContext tokenValidationContext) {
            }
        };
    }

}
