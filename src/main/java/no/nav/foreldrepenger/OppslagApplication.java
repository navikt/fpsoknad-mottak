package no.nav.foreldrepenger;

import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableOIDCTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
public class OppslagApplication {

    public static void main(String[] args) {
        SpringApplication.run(OppslagApplication.class, args);
    }

}
