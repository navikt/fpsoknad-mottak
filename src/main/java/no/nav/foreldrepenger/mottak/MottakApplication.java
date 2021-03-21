package no.nav.foreldrepenger.mottak;

import static no.nav.foreldrepenger.boot.conditionals.Cluster.profiler;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;

@EnableJwtTokenValidation(ignore = { "graphql.spring", "org.springframework", "springfox.documentation" })
@SpringBootApplication
@EnableCaching
@EnableRetry
@EnableKafka
@ConfigurationPropertiesScan
public class MottakApplication {
    public static void main(String[] args) {
        LoggerFactory.getLogger(MottakApplication.class).info(System.getenv().toString());
        new SpringApplicationBuilder(MottakApplication.class)
                .profiles(profiler())
                .main(MottakApplication.class)
                .run(args);
    }
}