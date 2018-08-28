package no.nav.foreldrepenger.mottak.config;

import static java.util.stream.Collectors.toSet;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.ServletContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.models.Scheme;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket docket(ServletContext context) {
        return new Docket(SWAGGER_2)
                // .protocols(protocols(HTTPS, HTTP))
                .select()
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.any())
                .build();
    }

    private static Set<String> protocols(Scheme... schemes) {
        return Stream.of(schemes)
                .map(s -> s.toValue())
                .collect(toSet());
    }
}
