package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedToVedlegg;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FordelMetadata;

public class TestMetadata {

    private static final String ID = "123";
    private static final String REF = "42";
    private static final AktørId AKTOR_ID = new AktørId(ID);

    @Test
    public void testIgnorerIkkeLastetOpp() {
        Søknad søknad = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(DEFAULT_VERSJON);
        FordelMetadata metadata = new FordelMetadata(søknad, INITIELL_FORELDREPENGER, AKTOR_ID, REF);
        assertEquals(2, søknad.getVedlegg().size());
        assertEquals(3, metadata.getFiler().size());
        assertEquals(ID, metadata.getBrukerId());
    }

    @Test
    public void test2LastetOpp() {
        Søknad søknad = søknadMedToVedlegg(DEFAULT_VERSJON);
        FordelMetadata metadata = new FordelMetadata(søknad, ENDRING_FORELDREPENGER, AKTOR_ID, REF);
        assertEquals(2, søknad.getVedlegg().size());
        assertEquals(4, metadata.getFiler().size());
    }

    @Test
    public void test2ES() {
        Søknad søknad = TestUtils.engangssøknad(ForeldrepengerTestUtils.V3);
        FordelMetadata metadata = new FordelMetadata(søknad, INITIELL_ENGANGSSTØNAD, AKTOR_ID, REF);
        assertEquals(1, søknad.getVedlegg().size());
        assertEquals(3, metadata.getFiler().size());
    }
}
