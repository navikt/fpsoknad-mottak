package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.NORSK_FORELDER_FNR;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedToVedlegg;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.DelegerendeDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.V1DomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.V1XMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.DelegerendeXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.XMLMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FPV1JAXBUtilTest {

    private static final AktorId SØKER = new AktorId("42");

    private static final AktorId ID = SØKER;

    @Mock
    private Oppslag oppslag;
    private DelegerendeDomainMapper domainMapper;
    private XMLMapper xmlMapper;

    @Before
    public void before() {
        when(oppslag.getFnr(eq(ID))).thenReturn(NORSK_FORELDER_FNR);
        when(oppslag.getAktørId(eq(NORSK_FORELDER_FNR))).thenReturn(ID);
        domainMapper = new DelegerendeDomainMapper(new V1DomainMapper(oppslag));
        xmlMapper = new DelegerendeXMLMapper(new V1XMLMapper(oppslag));
    }

    @Test
    public void testFørstegangssøknadRoundtrip() {
        Søknad søknad = søknadMedToVedlegg(Versjon.V1);
        assertEquals(søknad, xmlMapper.tilSøknad(domainMapper.tilXML(søknad, SØKER, Versjon.V1)));
    }
}
