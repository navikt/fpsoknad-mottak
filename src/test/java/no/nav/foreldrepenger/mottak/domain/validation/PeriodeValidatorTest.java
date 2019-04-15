package no.nav.foreldrepenger.mottak.domain.validation;

import static no.nav.foreldrepenger.mottak.domain.felles.medlemskap.ArbeidsInformasjon.ARBEIDET_I_UTLANDET;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Fordeling;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UttaksPeriode;

public class PeriodeValidatorTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testIkkeOverlappendeFortid() {
        LukketPeriode periode1 = new LukketPeriode(now().minusMonths(6), now());
        LukketPeriode periode2 = new LukketPeriode(now().minusYears(1), now().minusMonths(6).minusDays(1));
        TidligereOppholdsInformasjon tidligere = new TidligereOppholdsInformasjon(ARBEIDET_I_UTLANDET,
                opphold(periode2, periode1));
        Set<ConstraintViolation<TidligereOppholdsInformasjon>> constraintViolations = validator.validate(tidligere);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void testIkkeOverlappendeFramtidig() {
        LukketPeriode periode1 = new LukketPeriode(now(), now().plusMonths(6));
        LukketPeriode periode2 = new LukketPeriode(now().plusMonths(6).plusDays(1), now().plusYears(1));
        FramtidigOppholdsInformasjon framtidig = new FramtidigOppholdsInformasjon(
                opphold(periode2, periode1));
        Set<ConstraintViolation<FramtidigOppholdsInformasjon>> constraintViolations = validator.validate(framtidig);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void testIkkeOverlappendeMenFortid() {
        LukketPeriode periode1 = new LukketPeriode(now().minusMonths(6), now());
        LukketPeriode periode2 = new LukketPeriode(now().minusYears(1), now().minusMonths(6).minusDays(1));
        FramtidigOppholdsInformasjon framtidig = new FramtidigOppholdsInformasjon(
                opphold(periode2, periode1));
        Set<ConstraintViolation<FramtidigOppholdsInformasjon>> constraintViolations = validator.validate(framtidig);
        assertFalse(constraintViolations.isEmpty());
    }

    @Test
    public void testStartStoppFeil() {
        LukketPeriode periode1 = new LukketPeriode(now().plusMonths(6), now());
        Set<ConstraintViolation<LukketPeriode>> constraintViolations = validator.validate(periode1);
        assertFalse(constraintViolations.isEmpty());
    }

    @Test
    public void testIkkeOverlappendeMenFramtid() {
        LukketPeriode periode1 = new LukketPeriode(now(), now().plusMonths(6));
        LukketPeriode periode2 = new LukketPeriode(now().plusMonths(6).plusDays(1), now().plusYears(1));
        TidligereOppholdsInformasjon framtidig = new TidligereOppholdsInformasjon(ARBEIDET_I_UTLANDET,
                opphold(periode2, periode1));
        Set<ConstraintViolation<TidligereOppholdsInformasjon>> constraintViolations = validator.validate(framtidig);
        assertFalse(constraintViolations.isEmpty());
    }

    @Test
    public void testLukketPeriodeMedVedleggOK() {
        LukketPeriodeMedVedlegg periode1 = new UttaksPeriode(now(), now().plusMonths(6), StønadskontoType.FEDREKVOTE,
                true,
                MorsAktivitet.ARBEID, true, new ProsentAndel(100.0d),
                Collections.emptyList());
        Set<ConstraintViolation<LukketPeriodeMedVedlegg>> constraintViolations = validator.validate(periode1);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void testLukketPeriodeMedVedleggNull() {
        LukketPeriodeMedVedlegg periode1 = new UttaksPeriode(now(), null, StønadskontoType.FEDREKVOTE,
                true,
                MorsAktivitet.ARBEID, true, new ProsentAndel(100.0d),
                Collections.emptyList());
        Set<ConstraintViolation<LukketPeriodeMedVedlegg>> constraintViolations = validator.validate(periode1);
        assertFalse(constraintViolations.isEmpty());
    }

    @Test
    public void testLukketPeriodeMedVedleggFomNull() {
        LukketPeriodeMedVedlegg periode1 = new UttaksPeriode(null, now(), StønadskontoType.FEDREKVOTE,
                true,
                MorsAktivitet.ARBEID, true, new ProsentAndel(100.0d),
                Collections.emptyList());
        Set<ConstraintViolation<LukketPeriodeMedVedlegg>> constraintViolations = validator.validate(periode1);
        assertFalse(constraintViolations.isEmpty());
    }

    @Test
    public void testLukketPeriodeMedVedleggFomFørTom() {
        LukketPeriodeMedVedlegg periode1 = new UttaksPeriode(now(), now().minusDays(1), StønadskontoType.FEDREKVOTE,
                true,
                MorsAktivitet.ARBEID, true, new ProsentAndel(100.0d),
                Collections.emptyList());
        Set<ConstraintViolation<LukketPeriodeMedVedlegg>> constraintViolations = validator.validate(periode1);
        assertFalse(constraintViolations.isEmpty());
    }

    @Test
    public void testLukketPeriodeMedVedleggFomOgTomLike() {
        LukketPeriodeMedVedlegg periode1 = uttaksPeriode(now(), now());
        Set<ConstraintViolation<LukketPeriodeMedVedlegg>> constraintViolations = validator.validate(periode1);
        assertTrue(constraintViolations.isEmpty());
    }

    private static UttaksPeriode uttaksPeriode(LocalDate fom, LocalDate tom) {
        return new UttaksPeriode(fom, tom, StønadskontoType.FEDREKVOTE,
                true,
                MorsAktivitet.ARBEID, true, new ProsentAndel(100.0d),
                Collections.emptyList());
    }

    @Test
    public void testFordeling() {
        Fordeling fordeling = new Fordeling(true,
                Overføringsårsak.ALENEOMSORG,
                Collections.singletonList(uttaksPeriode(null, null)));
        Set<ConstraintViolation<Fordeling>> constraintViolations = validator.validate(fordeling);
        assertFalse(constraintViolations.isEmpty());
        Endringssøknad es = new Endringssøknad(LocalDate.now(), new Søker(BrukerRolle.MOR), fordeling, null, null,
                null, "42");
        assertFalse(validator.validate(es).isEmpty());
    }

    @Test
    public void testOverlappendeFortid() {
        LukketPeriode periode1 = new LukketPeriode(now().minusMonths(6), now());
        LukketPeriode periode2 = new LukketPeriode(now().minusYears(1), now().minusMonths(4));
        LukketPeriode periode3 = new LukketPeriode(now().minusMonths(4), now());
        TidligereOppholdsInformasjon tidligere = new TidligereOppholdsInformasjon(ARBEIDET_I_UTLANDET,
                opphold(periode1, periode2, periode3));
        assertFalse(validator.validate(tidligere).isEmpty());
    }

    private static LocalDate now() {
        return LocalDate.now();
    }

    private static List<Utenlandsopphold> opphold(LukketPeriode... perioder) {
        return Arrays.stream(perioder).map(s -> new Utenlandsopphold(CountryCode.SE, s)).collect(Collectors.toList());
    }
}
