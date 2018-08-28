package no.nav.foreldrepenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import no.nav.security.oidc.test.support.spring.TokenGeneratorController;

@SpringBootApplication
@no.nav.security.spring.oidc.api.EnableOIDCTokenValidation(ignore = "org.springframework")
@Import(value = TokenGeneratorController.class)
public class OppslagApplicationLocal {

    public static void main(String[] args) {
        SpringApplication.run(OppslagApplicationLocal.class, args);
    }

}
