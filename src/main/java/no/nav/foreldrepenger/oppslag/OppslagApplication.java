package no.nav.foreldrepenger.oppslag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.security.spring.oidc.validation.api.EnableOIDCTokenValidation;

@SpringBootApplication
@EnableOIDCTokenValidation(ignore="org.springframework")
public class OppslagApplication {

    public static void main(String[] args) {
        SpringApplication.run(OppslagApplication.class, args);
    }
}
