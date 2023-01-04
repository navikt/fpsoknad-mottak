package no.nav.foreldrepenger.mottak.innsending.pdf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.Person;

class SøknadTextFormatterTest {

    @Test
    void capitalize() {
        assertEquals("Enum to string", new SøknadTextFormatter(null, null).capitalize("ENUM_TO_STRING"));
    }

    @Test
    void datesMayBeNull() {
        assertEquals("", new SøknadTextFormatter(null, null).dato(null));
    }

    @Test
    void sammensattNavnFulltNavn() {
        var søknadTextFormatter = new SøknadTextFormatter(null, null);
        var navn = new Navn("Fornavn", "Mellomnavn", "Etternavn");
        var person = new Person(null, null, null, navn, null, null, null, null, null);

        var sammensattNavnFraNavn = søknadTextFormatter.sammensattNavn(navn);
        var sammensattNavnfraPerson = søknadTextFormatter.sammensattNavn(person);

        assertThat(sammensattNavnFraNavn)
            .isEqualTo(sammensattNavnfraPerson)
            .isEqualTo("Fornavn Mellomnavn Etternavn");
    }

    @Test
    void sammensattNavnIngenMellomnavn() {
        var søknadTextFormatter = new SøknadTextFormatter(null, null);
        var navn = new Navn("Fornavn", null, "Etternavn");
        var person = new Person(null, null, null, navn, null, null, null, null, null);

        var sammensattNavnFraNavn = søknadTextFormatter.sammensattNavn(navn);
        var sammensattNavnfraPerson = søknadTextFormatter.sammensattNavn(person);

        assertThat(sammensattNavnFraNavn)
            .isEqualTo(sammensattNavnfraPerson)
            .isEqualTo("Fornavn Etternavn");
    }

    @Test
    void sammensattNavnFraNavnNull() {
        var søknadTextFormatter = new SøknadTextFormatter(null, null);
        var navn = new Navn("", null, null);
        var person = new Person(null, null, null, navn, null, null, null, null, null);

        var sammensattNavnFraNavn = søknadTextFormatter.sammensattNavn(navn);
        var sammensattNavnfraPerson = søknadTextFormatter.sammensattNavn(person);

        assertThat(sammensattNavnFraNavn)
            .isEqualTo(sammensattNavnfraPerson)
            .isEmpty();
    }

    @Test
    void sammensattNavnFraPersonHvorNavnErNullHiverIkkeException() {
        var søknadTextFormatter = new SøknadTextFormatter(null, null);
        var person = new Person(null, null, null, null, null, null, null, null, null);

        var sammensattNavnfraPerson = søknadTextFormatter.sammensattNavn(person);

        assertThat(sammensattNavnfraPerson)
            .isEmpty();
    }
}
