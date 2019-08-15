package no.nav.foreldrepenger.mottak;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

import no.nav.foreldrepenger.mottak.config.ClusterAwareSpringProfileResolver;
import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;

@EnableOIDCTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
@SpringBootApplication
@EnableCaching
@EnableKafka
public class MottakApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MottakApplication.class)
                .profiles(new ClusterAwareSpringProfileResolver().getProfile())
                .main(MottakApplication.class)
                .run(args);
    }
}