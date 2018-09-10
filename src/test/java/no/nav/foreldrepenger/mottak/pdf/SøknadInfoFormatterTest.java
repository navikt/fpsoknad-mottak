package no.nav.foreldrepenger.mottak.pdf;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Regnskapsfører;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SøknadInfoFormatterTest {

    @Test
    public void regnskapsførerListMayBeNull() {
        List<Regnskapsfører> regnskapsførerList = null;
        SøknadInfoFormatter formatter = new SøknadInfoFormatter(null, null, null);
        String name = formatter.navnToString(regnskapsførerList);
        assertEquals("ukjent", name);
    }


    @Test
    public void capitalize() {
        String orig = "ENUM_TO_STRING";
        SøknadInfoFormatter formatter = new SøknadInfoFormatter(null, null, null);
        assertEquals("Enum to string", formatter.capitalize(orig));
    }

}
