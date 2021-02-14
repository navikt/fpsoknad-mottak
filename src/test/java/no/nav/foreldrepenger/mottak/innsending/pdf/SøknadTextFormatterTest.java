package no.nav.foreldrepenger.mottak.innsending.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SøknadTextFormatterTest {

    @Test
    void capitalize() {
        assertEquals("Enum to string", new SøknadTextFormatter(null, null).capitalize("ENUM_TO_STRING"));
    }

    @Test
    void datesMayBeNull() {
        assertEquals("", new SøknadTextFormatter(null, null).dato(null));
    }

}
