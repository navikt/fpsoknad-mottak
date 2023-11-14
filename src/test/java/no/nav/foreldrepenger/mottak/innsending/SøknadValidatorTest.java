package no.nav.foreldrepenger.mottak.innsending;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UttaksPeriode;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;

class SøknadValidatorTest {

    @Test
    void overlapp_1() {
        var p1 = getUttaksPeriode(LocalDate.now(), LocalDate.now().plusWeeks(1));
        var p2 = getUttaksPeriode(p1.getTom(), p1.getTom().plusWeeks(1));
        assertThat(SøknadValidator.finnesOverlapp(List.of(p1, p2))).isTrue();
    }

    @Test
    void overlapp_2() {
        var p1 = getUttaksPeriode(LocalDate.now(), LocalDate.now().plusWeeks(1));
        var p2 = getUttaksPeriode(p1.getTom().plusDays(1), p1.getTom().plusWeeks(1));
        var p3 = getUttaksPeriode(p2.getTom().plusDays(1), p2.getTom().plusWeeks(1));
        assertThat(SøknadValidator.finnesOverlapp(List.of(p1, p2, p3))).isFalse();
    }

    @Test
    void overlapp_3() {
        var p1 = getUttaksPeriode(LocalDate.now(), LocalDate.now().plusWeeks(1));
        var p2 = getUttaksPeriode(p1.getTom().plusDays(1), p1.getTom().plusWeeks(1));
        var p3 = getUttaksPeriode(p2.getTom().minusDays(2), p2.getTom().plusWeeks(1));
        assertThat(SøknadValidator.finnesOverlapp(List.of(p1, p2, p3))).isTrue();
    }

    @Test
    void adopsjon_ulik_antall_barn_og_fødselsdatoer_skal_gi_feil() {
        var adopsjon = new Adopsjon(2, null, false, false, LocalDate.now(), List.of(LocalDate.now()));
        assertThatThrownBy(() -> SøknadValidator.validerAdopsjon(adopsjon)).isInstanceOf(UnexpectedInputException.class);
    }

    @Test
    void adopsjon_fødselsdatoer_null_skal_ikke_gi_exception() {
        var adopsjon = new Adopsjon(2, null, false, false, LocalDate.now(), null);
        assertThatCode(() -> SøknadValidator.validerAdopsjon(adopsjon)).doesNotThrowAnyException();
    }

    @Test
    void adopsjon_likt_antall_barn_og_fødselsdatoer_skal_ikke_gi_feil() {
        var adopsjon = new Adopsjon(2, null, false, false, LocalDate.now(), List.of(LocalDate.now(), LocalDate.now().minusYears(1)));
        assertThatCode(() -> SøknadValidator.validerAdopsjon(adopsjon)).doesNotThrowAnyException();
    }

    private static UttaksPeriode getUttaksPeriode(LocalDate fom, LocalDate tom) {
        return new UttaksPeriode(fom, tom, StønadskontoType.FELLESPERIODE, false, null, false, null, false);
    }

}
