package no.nav.foreldrepenger.mottak.pdf;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ForeldrepengeinfoRendererTest {

    @Test
    public void regnskapsførerListMayBeNull() {
        ForeldrepengeInfoRenderer fpRenderer = new ForeldrepengeInfoRenderer(null, null);
        String name = fpRenderer.regnskapsførere(null);
        assertEquals("ukjent", name);
    }
}
