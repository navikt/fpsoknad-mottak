package no.nav.foreldrepenger.mottak.config;

import static com.google.common.base.Predicates.or;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static no.nav.foreldrepenger.mottak.innsending.SøknadController.INNSENDING;
import static no.nav.foreldrepenger.mottak.innsending.SøknadDevController.INNSENDING_PREPROD;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynController.INNSYN;
import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .protocols(protocols("http", "https"))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(or(
                        regex(INNSENDING_PREPROD + ".*"),
                        regex(INNSYN + ".*"),
                        regex(INNSENDING + ".*")))
                .build();
    }

    private static Set<String> protocols(String... schemes) {
        return stream(schemes)
                .collect(toSet());
    }
}