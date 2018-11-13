package no.nav.foreldrepenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;

@SpringBootApplication
@EnableRetry
@EnableOIDCTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
public class OppslagApplication {

    public static void main(String[] args) {
        SpringApplication.run(OppslagApplication.class, args);
    }

}
