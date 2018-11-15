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
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.ForeldrepengerSøknadMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JaxbTest {

    private static final AktorId SØKER = new AktorId("42");

    private static final AktorId ID = SØKER;

    @Mock
    private Oppslag oppslag;
    private ForeldrepengerSøknadMapper mapper;

    @Before
    public void before() {
        when(oppslag.getFnr(eq(ID))).thenReturn(NORSK_FORELDER_FNR);
        when(oppslag.getAktørId(eq(NORSK_FORELDER_FNR))).thenReturn(ID);
        mapper = new ForeldrepengerSøknadMapper(oppslag);
    }

    @Test
    public void testFørstegangssøknadRoundtrip() {
        Søknad søknad = søknadMedToVedlegg();
        assertEquals(søknad, mapper.tilSøknad(mapper.tilXML(søknad, SØKER)));
    }
}
