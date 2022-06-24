package no.nav.foreldrepenger.mottak.http;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.Valid;

import org.junit.jupiter.api.Test;

class RestApiInputValideringAnnoteringTest extends RestApiTestUtil {


    @Test
    void sjekkAtAlleEndepunktHarInputValidering() {
        for (var method : finnAlleRestMetoder()) {
            for (var i = 0; i < method.getParameterCount(); i++) {
                assertThat(method.getParameterTypes()[i].isAssignableFrom(String.class)).as(
                        "REST-metoder skal ikke har parameter som er String eller mer generelt. Bruk DTO-er og valider. "
                            + printKlasseOgMetodeNavn.apply(method))
                    .isFalse();
                assertThat(method.getParameters()[i].isAnnotationPresent(Valid.class))
                    .as("Alle parameter for REST-metoder skal være annotert med @Valid. Var ikke det for "
                        + printKlasseOgMetodeNavn.apply(method))
                    .withFailMessage("Fant parametere som mangler @Valid annotation '" + method.getParameters()[i].toString() + "'").isTrue();
            }
        }
    }
}
