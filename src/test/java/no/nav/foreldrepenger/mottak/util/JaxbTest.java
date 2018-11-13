package no.nav.foreldrepenger.mottak.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import javax.xml.bind.Marshaller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadTilXMLMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.Jaxb.ValidationMode;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JaxbTest {

    private static final Fødselsnummer FNR = new Fødselsnummer("42");
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
        Marshaller marshaller = Jaxb.marshaller(ValidationMode.FORELDREPENGER);
        // marshaller.setSchema(Jaxb.FP_SCHEMA);
        Soeknad mdodel = new SøknadTilXMLMapper(oppslag).tilModell(søknad, new AktorId("42"));
        marshaller.marshal(mdodel, System.out);
    }
}
