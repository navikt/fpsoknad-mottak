package no.nav.foreldrepenger.mottak.innsending.pdf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.domain.Navn;

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

        var sammensattNavnFraNavn = søknadTextFormatter.sammensattNavn(navn);

        assertThat(sammensattNavnFraNavn).isEqualTo("Fornavn Mellomnavn Etternavn");
    }

    @Test
    void sammensattNavnIngenMellomnavn() {
        var søknadTextFormatter = new SøknadTextFormatter(null, null);
        var navn = new Navn("Fornavn", null, "Etternavn");

        var sammensattNavnFraNavn = søknadTextFormatter.sammensattNavn(navn);

        assertThat(sammensattNavnFraNavn).isEqualTo("Fornavn Etternavn");
    }

    @Test
    void sammensattNavnFraNavnNull() {
        var søknadTextFormatter = new SøknadTextFormatter(null, null);
        var navn = new Navn("", null, null);

        var sammensattNavnFraNavn = søknadTextFormatter.sammensattNavn(navn);

        assertThat(sammensattNavnFraNavn).isEmpty();
    }
}
