package no.nav.foreldrepenger.mottak.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadTilXMLMapper;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.XMLTilSøknadMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JaxbTest {

    private static final Fødselsnummer FNR = new Fødselsnummer("01010111111");
    private static final AktorId ID = new AktorId("42");

    @Mock
    private Oppslag oppslag;

    @Before
    public void before() {
        when(oppslag.getFnr(eq(ID))).thenReturn(FNR);
        when(oppslag.getAktørId(any(Fødselsnummer.class))).thenReturn(ID);
    }

    @Test
    public void testSerialization() throws Exception {
        Søknad søknad = ForeldrepengerTestUtils.søknadMedToVedlegg();
        SøknadTilXMLMapper søknadTilXMLMapper = new SøknadTilXMLMapper(oppslag);
        String xml = søknadTilXMLMapper.tilXML(søknad, new AktorId("42"));
        XMLTilSøknadMapper fraXMLMapper = new XMLTilSøknadMapper(oppslag);
        System.out.println(xml);
        Søknad retur = fraXMLMapper.tilSøknad(xml);
        Foreldrepenger fp1 = Foreldrepenger.class.cast(søknad.getYtelse());
        Foreldrepenger fp2 = Foreldrepenger.class.cast(retur.getYtelse());
        assertEquals(fp1.getAnnenForelder(), fp2.getAnnenForelder());
        assertEquals(fp1.getDekningsgrad(), fp2.getDekningsgrad());
        assertEquals(fp1.getFordeling(), fp2.getFordeling());
        assertEquals(fp1.getMedlemsskap(), fp2.getMedlemsskap());
        assertEquals(fp1.getOpptjening(), fp2.getOpptjening());
        assertEquals(fp1.getRelasjonTilBarn(), fp2.getRelasjonTilBarn());
        assertEquals(fp1.getRettigheter(), fp2.getRettigheter());

    }
}
