package no.nav.foreldrepenger.mottak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Fpsoknad-mottak")
                .description("Mottar søknader om svangerskapspenger, foreldrepenger og engangsstønad fra frontend og sender dem videre inn i NAV for behandling")
                .version("v0.0.1")
                .license(new License().name("MIT").url("http://nav.no")));
    }
}
