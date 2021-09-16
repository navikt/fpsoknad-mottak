package no.nav.foreldrepenger.mottak.innsending.innsyn;

import static no.nav.foreldrepenger.common.domain.FagsakType.ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.domain.FagsakType.FORELDREPENGER;
import static no.nav.foreldrepenger.common.util.Versjon.V1;
import static no.nav.foreldrepenger.common.util.Versjon.V2;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.load;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.XMLStreamVedtakInspektør;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers.DelegerendeXMLVedtakMapper;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers.V1EngangsstønadXMLVedtakMapper;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers.V2XMLVedtakMapper;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers.XMLVedtakMapper;

class VedtakTest {

    private static final XMLVedtakMapper MAPPER = new DelegerendeXMLVedtakMapper(new V1EngangsstønadXMLVedtakMapper(),
            new V2XMLVedtakMapper());
    private static final Inspektør INSPEKTØR = new XMLStreamVedtakInspektør();

    @Test
    void testVedtakFPV2() throws IOException {
        String xml = load("xml/FPVedtakV2.xml");
        SøknadEgenskap e = INSPEKTØR.inspiser(xml);
        assertEquals(V2, e.getVersjon());
        assertEquals(FORELDREPENGER, e.getFagsakType());
        Vedtak vedtak = MAPPER.tilVedtak(xml, e);
        assertNotNull(vedtak);
        assertNotNull(vedtak.getUttak());
    }

    @Test
    void testVedtakESV1() throws IOException {
        String xml = load("xml/ESVedtakV1.xml");
        SøknadEgenskap e = INSPEKTØR.inspiser(xml);
        assertEquals(V1, e.getVersjon());
        assertEquals(ENGANGSSTØNAD, e.getFagsakType());
        Vedtak vedtak = MAPPER.tilVedtak(xml, e);
        assertNotNull(vedtak);
    }

    @Test
    void testVedtakESV2() throws IOException {
        String xml = load("xml/ESVedtakV2.xml");
        SøknadEgenskap e = INSPEKTØR.inspiser(xml);
        assertEquals(V2, e.getVersjon());
        assertEquals(ENGANGSSTØNAD, e.getFagsakType());
        Vedtak vedtak = MAPPER.tilVedtak(xml, e);
        assertNotNull(vedtak);
    }
}
