package no.nav.foreldrepenger.mottak.innsending.innsyn;

import static no.nav.foreldrepenger.mottak.domain.FagsakType.ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.domain.FagsakType.FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.load;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.XMLInspektør;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.XMLStreamVedtakInspektør;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers.DelegerendeXMLVedtakMapper;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers.V1XMLVedtakMapper;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers.V2XMLVedtakMapper;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers.XMLVedtakMapper;

class VedtakTest {

    private static final XMLVedtakMapper MAPPER = new DelegerendeXMLVedtakMapper(new V1XMLVedtakMapper(),
            new V2XMLVedtakMapper());
    private static final XMLInspektør INSPEKTØR = new XMLStreamVedtakInspektør();

    @Test
    void testVedtakFPV2() throws IOException {
        String xml = load("FPVedtakV2.xml");
        SøknadEgenskap e = INSPEKTØR.inspiser(xml);
        assertEquals(V2, e.getVersjon());
        assertEquals(FORELDREPENGER, e.getFagsakType());
        Vedtak vedtak = MAPPER.tilVedtak(xml, e);
        assertNotNull(vedtak);
        assertNotNull(vedtak.getUttak());
    }

    @Test
    void testVedtakESV1() throws IOException {
        String xml = load("ESVedtakV1.xml");
        SøknadEgenskap e = INSPEKTØR.inspiser(xml);
        assertEquals(V1, e.getVersjon());
        assertEquals(ENGANGSSTØNAD, e.getFagsakType());
        Vedtak vedtak = MAPPER.tilVedtak(xml, e);
        assertNull(vedtak);
    }

    @Test
    void testVedtakESV2() throws IOException {
        String xml = load("ESVedtakV2.xml");
        SøknadEgenskap e = INSPEKTØR.inspiser(xml);
        assertEquals(V2, e.getVersjon());
        assertEquals(ENGANGSSTØNAD, e.getFagsakType());
        Vedtak vedtak = MAPPER.tilVedtak(xml, e);
        assertNull(vedtak);
    }
}
