package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedToVedlegg;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelMetadata;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class TestMetadata {

    private static final String REF = "42";
    private static final AktorId AKTOR_ID = new AktorId("123");

    @Test
    public void testIgnorerIkkeLastetOpp() {
        Versjon v = V1;
        Søknad søknad = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(v);
        FPFordelMetadata metadata = new FPFordelMetadata(søknad, SøknadType.INITIELL_FORELDREPENGER, AKTOR_ID, REF);
        assertEquals(2, søknad.getVedlegg().size());
        assertEquals(3, metadata.getFiler().size());
        assertEquals("123", metadata.getBrukerId());
    }

    @Test
    public void test2LastetOpp() {
        Versjon v = V2;
        Søknad søknad = søknadMedToVedlegg(v);
        FPFordelMetadata metadata = new FPFordelMetadata(søknad, SøknadType.ENDRING_FORELDREPENGER, AKTOR_ID, REF);
        assertEquals(2, søknad.getVedlegg().size());
        assertEquals(4, metadata.getFiler().size());
    }

    @Test
    public void test2ES() {
        Versjon v = V2;
        Søknad søknad = TestUtils.engangssøknad(v, ForeldrepengerTestUtils.V3);
        FPFordelMetadata metadata = new FPFordelMetadata(søknad, SøknadType.INITIELL_ENGANGSSTØNAD, AKTOR_ID, REF);
        System.out.println(metadata);
        assertEquals(1, søknad.getVedlegg().size());
        assertEquals(3, metadata.getFiler().size());
    }
}
