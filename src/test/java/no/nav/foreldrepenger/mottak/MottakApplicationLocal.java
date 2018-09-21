package no.nav.foreldrepenger.mottak;

import no.nav.security.oidc.test.support.spring.TokenGeneratorConfiguration;
import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@EnableOIDCTokenValidation(ignore = { "org.springframework" })
@SpringBootApplication
@Import(value = TokenGeneratorConfiguration.class)
@EnableSwagger2
@ComponentScan(excludeFilters = { @Filter(type = ASSIGNABLE_TYPE, value = MottakApplication.class) })
public class MottakApplicationLocal {

    public static void main(String[] args) {
        SpringApplication.run(MottakApplicationLocal.class, args);
    }


}
