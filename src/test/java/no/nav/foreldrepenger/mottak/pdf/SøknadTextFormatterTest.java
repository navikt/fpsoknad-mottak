package no.nav.foreldrepenger.mottak.pdf;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.innsending.pdf.SøknadTextFormatter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SøknadTextFormatterTest {

    @Test
    public void capitalize() {
        String orig = "ENUM_TO_STRING";
        SøknadTextFormatter formatter = new SøknadTextFormatter(null, null, CountryCode.NO);
        assertEquals("Enum to string", formatter.capitalize(orig));
    }

    @Test
    public void datesMayBeNull() {
        SøknadTextFormatter formatter = new SøknadTextFormatter(null, null, CountryCode.NO);
        String formatted = formatter.dato(null);
        assertEquals("?", formatted);
    }

}
