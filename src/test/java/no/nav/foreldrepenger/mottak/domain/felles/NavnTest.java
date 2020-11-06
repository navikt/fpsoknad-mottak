package no.nav.foreldrepenger.mottak.domain.felles;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.Navn;

public class NavnTest {

    @Test
    public void navnEquals() {
        var n = new Navn("Ole", "Mellomnavn", "Olsen", null);
        var n1 = new Navn("Ole", "Mellomnavn", "Olsen", Kjønn.M);
        assertEquals(n, n1);

    }
}
