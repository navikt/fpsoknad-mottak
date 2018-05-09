package no.nav.foreldrepenger.mottak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import no.nav.security.spring.oidc.test.TokenGeneratorConfiguration;
import no.nav.security.spring.oidc.validation.api.EnableOIDCTokenValidation;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableOIDCTokenValidation(ignore = { "org.springframework" })
@SpringBootApplication
@Import(value = TokenGeneratorConfiguration.class)
@EnableSwagger2
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MottakApplication.class) })
public class MottakApplicationLocal {

    public static void main(String[] args) {
        SpringApplication.run(MottakApplicationLocal.class, args);
    }
}