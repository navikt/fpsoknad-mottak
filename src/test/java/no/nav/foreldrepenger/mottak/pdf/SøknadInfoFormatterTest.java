package no.nav.foreldrepenger.mottak.pdf;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Regnskapsfører;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void datesMayBeNull() {
        List<Regnskapsfører> regnskapsførerList = null;
        SøknadTextFormatter formatter = new SøknadTextFormatter(null, null, CountryCode.NO);
        String formatted = formatter.date(null);
        assertEquals("?", formatted);
    }

}
