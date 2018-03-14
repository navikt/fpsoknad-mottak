package no.nav.foreldrepenger.oppslag;

import no.nav.security.spring.oidc.validation.api.EnableOIDCTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableOIDCTokenValidation(ignore="org.springframework")
public class OppslagApplicationLocal {

    public static void main(String[] args) {
        SpringApplication.run(OppslagApplicationLocal.class, args);
    }
}
