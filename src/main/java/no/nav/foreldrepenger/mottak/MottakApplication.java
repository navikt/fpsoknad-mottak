package no.nav.foreldrepenger.mottak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableOIDCTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
@SpringBootApplication
@EnableSwagger2
public class MottakApplication {

    public static void main(String[] args) {
        SpringApplication.run(MottakApplication.class, args);
    }
}