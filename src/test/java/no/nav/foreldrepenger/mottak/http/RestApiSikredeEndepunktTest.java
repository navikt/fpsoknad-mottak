package no.nav.foreldrepenger.mottak.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import no.nav.security.token.support.core.api.RequiredIssuers;
import no.nav.security.token.support.core.api.Unprotected;

class RestApiSikredeEndepunktTest extends RestApiTestUtil {

    private static final String ENDEPUNKT_SOM_KAN_VÆRE_UNPROTECTED = "ping";

    @Test
    void sjekkAtProtectedRestControllerIkkeHarUbeskyttetAnnotering() {
        assertThat(ProtectedRestController.class)
            .hasAnnotations(RestController.class, RequiredIssuers.class, Validated.class);
        assertThat(ProtectedRestController.class.getAnnotation(RequiredIssuers.class).value())
            .hasSizeGreaterThan(0);
        assertThat(ProtectedRestController.class.isAnnotationPresent(Unprotected.class))
            .as("Sørg for at @ProtectedRestController ikke er annotert med @Unprotected!")
            .isFalse();
    }


    @Test
    void sjekkAtAlleEndepunktErBeskyttet() {
        for (var metode : finnAlleRestMetoder()) {
            assertThat(erEndepunktUnprotected(metode))
                .as("Bare ping endepunkt skal være ubeskyttet. Følgende endepunkt er ikke " + printKlasseOgMetodeNavn.apply(metode))
                .isFalse();
        }
    }

    @Test
    void sjekkAtRestControllerErBeskyttet() {
        for (var klasse : hentAlleRestControllerKlasser()) {
            assertThat(erRestControllerUbeskyttet(klasse))
                .as("RestController er annotert med @Unprotected eller mangler @ProtectedRestController og er ubeskyttet: " + klasse.getName())
                .isFalse();
        }
    }

    private boolean erRestControllerUbeskyttet(Class<?> klasse) {
        return klasse.isAnnotationPresent(Unprotected.class) || !klasse.isAnnotationPresent(ProtectedRestController.class);
    }

    private boolean erEndepunktUnprotected(Method metode) {
        if (metode.getName().equalsIgnoreCase(ENDEPUNKT_SOM_KAN_VÆRE_UNPROTECTED)) {
            return false;
        }
        return metode.isAnnotationPresent(Unprotected.class);
    }
}
