package no.nav.foreldrepenger.mottak.innsending.innsyn;

import static java.nio.charset.Charset.defaultCharset;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD_DOKMOT;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.UKJENT;
import static no.nav.foreldrepenger.common.util.Versjon.V1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;
import static org.springframework.util.StreamUtils.copyToString;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.core.io.ClassPathResource;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsyn.mappers.UkjentXMLSøknadMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V1EngangsstønadDokmotXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V1EngangsstønadPapirXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V1ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V2ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.XMLSøknadMapper;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.mappers.DelegerendeXMLSøknadMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)

public class SøknadInspektørTest {

    private static final AktørId AKTØRID = new AktørId("1111111111");
    private static final Fødselsnummer FNR = new Fødselsnummer("01010111111");

    @Mock
    private Oppslag oppslag;

    private XMLSøknadMapper mapper;
    private Inspektør inspektør;

    @BeforeEach
    public void beforeEach() {
        when(oppslag.aktørId(any(Fødselsnummer.class))).thenReturn(AKTØRID);
        when(oppslag.fnr(any(AktørId.class))).thenReturn(FNR);
        inspektør = new XMLStreamSøknadInspektør();
        mapper = new DelegerendeXMLSøknadMapper(
                new UkjentXMLSøknadMapper(),
                new V1EngangsstønadPapirXMLMapper(oppslag),
                new V1EngangsstønadDokmotXMLMapper(true),
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
        String xml = load("xml/v1fp.xml");
        SøknadEgenskap egenskap = inspektør.inspiser(xml);
        assertEquals(V1, egenskap.getVersjon());
        assertEquals(INITIELL_FORELDREPENGER, egenskap.getType());
        assertTrue(mapper.kanMappe(egenskap));
        assertNotNull(mapper.tilSøknad(xml, egenskap));
    }

    @Test
    public void testESDokmotV1XML() throws Exception {
        String xml = load("xml/esdokmotV1.xml");
        SøknadEgenskap egenskap = inspektør.inspiser(xml);
        assertEquals(V1, egenskap.getVersjon());
        assertEquals(INITIELL_ENGANGSSTØNAD_DOKMOT, egenskap.getType());
        assertTrue(mapper.kanMappe(egenskap));
        assertNotNull(mapper.tilSøknad(xml, egenskap));
    }

    // @Test
    public void testESPapirXML() throws Exception {
        String xml = load("xml/v1ESpapir.xml");
        SøknadEgenskap egenskap = inspektør.inspiser(xml);
        assertEquals(V1, egenskap.getVersjon());
        assertEquals(INITIELL_ENGANGSSTØNAD, egenskap.getType());
        assertTrue(mapper.kanMappe(egenskap));
        Søknad søknad = mapper.tilSøknad(xml, egenskap);
        assertNotNull(søknad);
    }

    @Test
    public void testInspektørWellFormedJunk() throws Exception {
        assertEquals(UKJENT, inspektør.inspiser(load("xml/junk.xml")));
    }

    private static String load(String file) throws IOException {
        return copyToString(new ClassPathResource(file).getInputStream(), defaultCharset());
    }
}
