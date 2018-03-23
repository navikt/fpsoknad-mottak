package no.nav.foreldrepenger.mottak.config;

import static com.google.common.base.Predicates.or;
import static io.swagger.models.Scheme.HTTP;
import static io.swagger.models.Scheme.HTTPS;
import static java.util.stream.Collectors.toSet;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.schema.AlternateTypeRules.newRule;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fasterxml.classmate.TypeResolver;

import io.swagger.models.Scheme;
import no.nav.foreldrepenger.mottak.http.DokmotMottakController;
import no.nav.foreldrepenger.mottak.http.DokmotMottakPreprodController;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket prodDocket() {
        return docket("prod")
                .paths(regex(DokmotMottakController.DOKMOT + ".*"))
                .build();

    }

    @Bean
    @Profile("preprod")
    public Docket preprodDocket() {
        return docket("preprod")
                .paths(or(
                        regex("/local" + ".*"),
                        regex(DokmotMottakController.DOKMOT + ".*"),
                        regex(DokmotMottakPreprodController.DOKMOT_PREPROD + ".*")))
                .build();
    }

    private ApiSelectorBuilder docket(String name) {
        return new Docket(SWAGGER_2)
                .groupName(name)
                .protocols(protocols(HTTPS, HTTP))
                .directModelSubstitute(LocalDate.class, String.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(List.class, LocalDate.class),
                                typeResolver.resolve(List.class, String.class)))
                .select()
                .apis(RequestHandlerSelectors.any());
    }

    private static Set<String> protocols(Scheme... schemes) {
        return Stream.of(schemes).map(s -> s.toValue()).collect(toSet());
    }
}
