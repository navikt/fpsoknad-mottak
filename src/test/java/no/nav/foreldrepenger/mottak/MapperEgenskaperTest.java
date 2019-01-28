package no.nav.foreldrepenger.mottak;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ETTERSENDING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V6;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public class MapperEgenskaperTest {
    @Test
    public void mapNotOK() {
        MapperEgenskaper egenskaper = new MapperEgenskaper(V6, INITIELL_ENGANGSSTØNAD);
        assertFalse(egenskaper.kanMappe(V1));
        assertFalse(egenskaper.kanMappe(new SøknadEgenskap(V1, ETTERSENDING_FORELDREPENGER)));
    }

    @Test
    public void mapOK() {
        MapperEgenskaper egenskaper = new MapperEgenskaper(V6, INITIELL_ENGANGSSTØNAD, ETTERSENDING_FORELDREPENGER);
        assertTrue(egenskaper.kanMappe(V6));
        assertTrue(egenskaper.kanMappe(new SøknadEgenskap(V6, ETTERSENDING_FORELDREPENGER)));
    }
}
