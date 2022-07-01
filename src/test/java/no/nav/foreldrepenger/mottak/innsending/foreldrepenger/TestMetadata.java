package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.VEDLEGG3;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.foreldrepengesøknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.foreldrepengesøknadMedToVedlegg;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.felles.TestUtils;

class TestMetadata {

    private static final String ID = "123";
    private static final String REF = "42";
    private static final AktørId AKTOR_ID = new AktørId(ID);

    @Test
    void testIgnorerIkkeLastetOpp() {
        var søknad = foreldrepengesøknadMedEttOpplastetEttIkkeOpplastetVedlegg();
        var metadata = new FordelMetadata(søknad, INITIELL_FORELDREPENGER, AKTOR_ID, REF);
        assertEquals(2, søknad.getVedlegg().size());
        assertEquals(3, metadata.filer().size());
        assertEquals(ID, metadata.brukerId());
    }

    @Test
    void test2LastetOpp() {
        var søknad = foreldrepengesøknadMedToVedlegg();
        var metadata = new FordelMetadata(søknad, ENDRING_FORELDREPENGER, AKTOR_ID, REF);
        assertEquals(2, søknad.getVedlegg().size());
        assertEquals(4, metadata.filer().size());
    }

    @Test
    void test2ES() {
        var søknad = TestUtils.engangssøknad(VEDLEGG3);
        var metadata = new FordelMetadata(søknad, INITIELL_ENGANGSSTØNAD, AKTOR_ID, REF);
        assertEquals(1, søknad.getVedlegg().size());
        assertEquals(3, metadata.filer().size());
    }
}
