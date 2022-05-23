package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.foreldrepengesøknad;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.svp;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;

class InspektørTest {

    @Test
    void verifiserAtSVPMatcherMedRiktigSøknadEgenskap() {
        assertThat(Inspektør.inspiser(svp())).isEqualTo(SøknadEgenskap.INITIELL_SVANGERSKAPSPENGER);
    }

    @Test
    void verifiserAtFPSøknadMatcherMedRiktigSøknadEgenskap() {
        assertThat(Inspektør.inspiser(foreldrepengesøknad(false))).isEqualTo(SøknadEgenskap.INITIELL_FORELDREPENGER);
    }
}
