package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.alleSøknadVersjoner;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.NORSK_FORELDER_FNR;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedToVedlegg;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsending.mappers.DelegerendeDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.mappers.DelegerendeXMLSøknadMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.V1ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.V2ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.V3ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.mappers.XMLSøknadMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
public class MapperRoundtripTest {

    private static final Inspektør INSPEKTØR = new XMLStreamSøknadInspektør();

    private static final AktørId SØKER = new AktørId("42");

    private static final AktørId ID = SØKER;

    @Mock
    private Oppslag oppslag;
    private DomainMapper domainMapper;
    private XMLSøknadMapper xmlMapper;

    @BeforeEach
    public void before() {
        when(oppslag.fnr(eq(ID))).thenReturn(NORSK_FORELDER_FNR);
        when(oppslag.aktørId(eq(NORSK_FORELDER_FNR))).thenReturn(ID);
        domainMapper = new DelegerendeDomainMapper(
                new V3ForeldrepengerDomainMapper(oppslag));
        xmlMapper = new DelegerendeXMLSøknadMapper(
                new V1ForeldrepengerXMLMapper(oppslag),
                new V2ForeldrepengerXMLMapper(oppslag),
                new V3ForeldrepengerXMLMapper(oppslag));
    }

    @Test
    public void testFørstegangssøknadRoundtrip() {
        alleSøknadVersjoner()
                .stream()
                .forEach(this::roundTripInitiell);
    }

    // @Test
    public void testEndringRoundtrip() {
        alleSøknadVersjoner()
                .stream()
                .forEach(this::roundTripEndring);
    }

    private void roundTripInitiell(Versjon v) {
        Søknad søknad = søknadMedToVedlegg(v);
        String xml = domainMapper.tilXML(søknad, SØKER, new SøknadEgenskap(v, SøknadType.INITIELL_FORELDREPENGER));
        SøknadEgenskap egenskaper = INSPEKTØR.inspiser(xml);
        assertEquals(søknad, xmlMapper.tilSøknad(xml, egenskaper));
    }

    private void roundTripEndring(Versjon v) {
        Endringssøknad søknad = endringssøknad(v);
        String xml = domainMapper.tilXML(søknad, SØKER, new SøknadEgenskap(v, SøknadType.ENDRING_FORELDREPENGER));
        SøknadEgenskap egenskaper = INSPEKTØR.inspiser(xml);
        assertEquals(søknad, xmlMapper.tilSøknad(xml, egenskaper));
    }
}
