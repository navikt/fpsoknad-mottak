package no.nav.foreldrepenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import no.nav.security.oidc.test.support.spring.TokenGeneratorConfiguration;

@SpringBootApplication
@no.nav.security.spring.oidc.api.EnableOIDCTokenValidation(ignore = { "org.springframework",
        "springfox.documentation" })
@Import(value = TokenGeneratorConfiguration.class)
public class OppslagApplicationLocal {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(OppslagApplicationLocal.class);
        app.setAdditionalProfiles("dev");
        app.run(args);
    }

}
