package no.nav.foreldrepenger.mottak.innsending.innsyn;

import static java.nio.charset.Charset.defaultCharset;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.UKJENT;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.util.StreamUtils.copyToString;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.SøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.mappers.DelegerendeXMLSøknadMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.DokmotV1XMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.UkjentXMLSøknadMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.V1ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.V2ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.XMLSøknadMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@ExtendWith(MockitoExtension.class)
public class SøknadInspektørTest {

    @Mock
    private Oppslag oppslag;

    private XMLSøknadMapper mapper;
    private SøknadInspektør inspektør;

    @BeforeEach
    public void beforeEach() {
        inspektør = new XMLStreamSøknadInspektør();
        mapper = new DelegerendeXMLSøknadMapper(
                new UkjentXMLSøknadMapper(),
                new DokmotV1XMLMapper(),
                new V1ForeldrepengerXMLMapper(oppslag),
                new V2ForeldrepengerXMLMapper(oppslag));
    }

    @Test
    public void testJunkXML() throws Exception {
        String xml = "junk";
        SøknadEgenskap egenskaper = inspektør.inspiser(xml);
        assertEquals(UKJENT, egenskaper);
        assertTrue(mapper.kanMappe(egenskaper));
        assertNull(mapper.tilSøknad(xml, egenskaper));
    }

    @Test
    public void testNullXML() throws Exception {
        SøknadEgenskap egenskaper = inspektør.inspiser((String) null);
        assertEquals(UKJENT, egenskaper);
        assertTrue(mapper.kanMappe(egenskaper));
        assertNull(mapper.tilSøknad(null, egenskaper));
    }

    @Test
    public void testFPV1XML() throws Exception {
        String xml = load("v1fp.xml");
        SøknadEgenskap egenskap = inspektør.inspiser(xml);
        assertEquals(V1, egenskap.getVersjon());
        assertEquals(INITIELL_FORELDREPENGER, egenskap.getType());
        assertTrue(mapper.kanMappe(egenskap));
        assertNotNull(mapper.tilSøknad(xml, egenskap));
    }

    @Test
    public void testESDokmotV1XML() throws Exception {
        String xml = load("esdokmotV1.xml");
        SøknadEgenskap egenskap = inspektør.inspiser(xml);
        assertEquals(V1, egenskap.getVersjon());
        assertEquals(INITIELL_ENGANGSSTØNAD, egenskap.getType());
        assertTrue(mapper.kanMappe(egenskap));
        assertNotNull(mapper.tilSøknad(xml, egenskap));
    }

    @Test
    public void testInspektørWellFormedJunk() throws Exception {
        assertEquals(UKJENT, inspektør.inspiser(load("junk.xml")));
    }

    private static String load(String file) throws IOException {
        return copyToString(new ClassPathResource(file).getInputStream(), defaultCharset());
    }
}
