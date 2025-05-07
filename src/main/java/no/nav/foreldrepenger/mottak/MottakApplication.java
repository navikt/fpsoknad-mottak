package no.nav.foreldrepenger.mottak;

import static no.nav.boot.conditionals.Cluster.profiler;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;

import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;

@EnableJwtTokenValidation(ignore = { "org.springframework", "org.springdoc" })
@SpringBootApplication
@EnableCaching
@EnableRetry
@EnableOAuth2Client(cacheEnabled = true)
@ConfigurationPropertiesScan
public class MottakApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MottakApplication.class)
                .profiles(profiler())
                .main(MottakApplication.class)
                .run(args);
    }
}
