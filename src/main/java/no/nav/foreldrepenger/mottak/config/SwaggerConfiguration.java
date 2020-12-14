package no.nav.foreldrepenger.mottak.config;

import static springfox.documentation.spi.DocumentationType.OAS_30;

import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.OpenIdConnectSchemeBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
public class SwaggerConfiguration {
    @Bean
    public Docket productApi() {
        return new Docket(OAS_30)
                .protocols(Set.of("http", "https"))
                .securityContexts(securityContexts())
                .securitySchemes(apiKeys())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private static List<SecurityScheme> apiKeys() {
        return List.of(new OpenIdConnectSchemeBuilder()
                .name("name")
                .openIdConnectUrl(
                        "https://navtestb2c.b2clogin.com/navtestb2c.onmicrosoft.com/v2.0/.well-known/openid-configuration?p=B2C_1A_idporten_ver1")
                .build());
    }

    private static List<SecurityContext> securityContexts() {
        return List.of(SecurityContext.builder().securityReferences(defaultAuth()).build());
    }

    private static List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("Authorization", authorizationScopes));
    }

}