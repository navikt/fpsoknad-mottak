package no.nav.foreldrepenger.mottak.pdf;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Regnskapsfører;

public class SøknadInfoFormatterTest {

    @Test
    public void regnskapsførerListMayBeNull() {
        List<Regnskapsfører> regnskapsførerList = null;
        SøknadTextFormatter formatter = new SøknadTextFormatter(null, null, CountryCode.NO);
        String name = formatter.regnskapsførere(regnskapsførerList);
        assertEquals("ukjent", name);
    }

    @Test
    public void capitalize() {
        String orig = "ENUM_TO_STRING";
        SøknadTextFormatter formatter = new SøknadTextFormatter(null, null, CountryCode.NO);
        assertEquals("Enum to string", formatter.capitalize(orig));
    }

}
