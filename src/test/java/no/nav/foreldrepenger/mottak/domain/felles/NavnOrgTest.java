package no.nav.foreldrepenger.common.domain.felles;

import static no.nav.foreldrepenger.common.domain.Orgnummer.MAGIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.felles.Kjønn;

class NavnOrgTest {

    @Test
    void navnEquals() {
        var n = new Navn("Ole", "Mellomnavn", "Olsen", null);
        var n1 = new Navn("Ole", "Mellomnavn", "Olsen", Kjønn.M);
        assertEquals(n, n1);

    }

    @Test
    void orgOK() {
        assertNotNull(new Orgnummer("993110469"));
        assertThrows(IllegalArgumentException.class, () -> new Orgnummer("123"));
        assertNotNull(new Orgnummer(MAGIC));
    }
}
