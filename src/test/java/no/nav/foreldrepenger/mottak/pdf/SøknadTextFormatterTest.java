package no.nav.foreldrepenger.mottak.pdf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import no.nav.foreldrepenger.mottak.innsending.pdf.SøknadTextFormatter;

public class SøknadTextFormatterTest {

    @Test
    public void capitalize() {
        String orig = "ENUM_TO_STRING";
        SøknadTextFormatter formatter = new SøknadTextFormatter(null, null);
        assertEquals("Enum to string", formatter.capitalize(orig));
    }

    @Test
    public void datesMayBeNull() {
        SøknadTextFormatter formatter = new SøknadTextFormatter(null, null);
        String formatted = formatter.dato(null);
        assertEquals("?", formatted);
    }

}
