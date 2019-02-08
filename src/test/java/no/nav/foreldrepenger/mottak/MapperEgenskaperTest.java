package no.nav.foreldrepenger.mottak;

import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ETTERSENDING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V20180924;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public class MapperEgenskaperTest {
    @Test
    public void mapNotOK() {
        MapperEgenskaper egenskaper = new MapperEgenskaper(V20180924, INITIELL_ENGANGSSTØNAD);
        assertFalse(egenskaper.kanMappe(V1));
        assertFalse(egenskaper.kanMappe(new SøknadEgenskap(V1, ETTERSENDING_FORELDREPENGER)));
    }

    @Test
    public void mapOK() {
        MapperEgenskaper egenskaper = new MapperEgenskaper(V20180924, INITIELL_ENGANGSSTØNAD,
                ETTERSENDING_FORELDREPENGER);
        assertTrue(egenskaper.kanMappe(V20180924));
        assertTrue(egenskaper.kanMappe(new SøknadEgenskap(V20180924, ETTERSENDING_FORELDREPENGER)));
    }
}
