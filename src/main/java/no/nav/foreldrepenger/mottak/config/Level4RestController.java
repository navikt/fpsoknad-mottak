package no.nav.foreldrepenger.mottak.config;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RestController;

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;

@Retention(RUNTIME)
@Target({ TYPE })
@RestController()
@ProtectedWithClaims(claimMap = "acr=Level4", issuer = "selvbetjening")
public @interface Level4RestController {
    @AliasFor(annotation = RestController.class, attribute = "path")
    String path() default "/";

    @AliasFor(annotation = RestController.class, attribute = "produces")
    String produces() default APPLICATION_JSON_VALUE;
}
