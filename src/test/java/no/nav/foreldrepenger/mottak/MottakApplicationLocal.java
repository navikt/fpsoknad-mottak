package no.nav.foreldrepenger.mottak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import no.nav.security.spring.oidc.test.TokenGeneratorConfiguration;
import no.nav.security.spring.oidc.validation.api.EnableOIDCTokenValidation;
@EnableOIDCTokenValidation(ignore="org.springframework")
@SpringBootApplication
@Import(value=TokenGeneratorConfiguration.class)
public class MottakApplicationLocal {

    public static void main(String[] args) {
        SpringApplication.run(MottakApplication.class, args);
    }
}