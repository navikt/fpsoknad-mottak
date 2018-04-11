package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.domain.ArbeidsInformasjon.ARBEIDET_I_UTLANDET;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.BeforeClass;
import org.junit.Test;

import com.neovisionaries.i18n.CountryCode;

public class PeriodeValidatorTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testIkkeOverlappendeFortid() {
        LukketPeriode periode1 = new LukketPeriode(now().minusMonths(6), now());
        LukketPeriode periode2 = new LukketPeriode(now().minusYears(1), now().minusMonths(6).minusDays(1));
        assertFalse(periode1.overlapper(periode2));
        assertFalse(periode2.overlapper(periode1));
        TidligereOppholdsInformasjon tidligere = new TidligereOppholdsInformasjon(true, ARBEIDET_I_UTLANDET,
                opphold(periode2, periode1));
        Set<ConstraintViolation<TidligereOppholdsInformasjon>> constraintViolations = validator.validate(tidligere);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void testIkkeOverlappendeFramtidig() {
        LukketPeriode periode1 = new LukketPeriode(now(), now().plusMonths(6));
        LukketPeriode periode2 = new LukketPeriode(now().plusMonths(6).plusDays(1), now().plusYears(1));
        assertFalse(periode1.overlapper(periode2));
        assertFalse(periode2.overlapper(periode1));
        FramtidigOppholdsInformasjon framtidig = new FramtidigOppholdsInformasjon(true, true,
                opphold(periode2, periode1));
        Set<ConstraintViolation<FramtidigOppholdsInformasjon>> constraintViolations = validator.validate(framtidig);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void testIkkeOverlappendeMenFortid() {
        LukketPeriode periode1 = new LukketPeriode(now().minusMonths(6), now());
        LukketPeriode periode2 = new LukketPeriode(now().minusYears(1), now().minusMonths(6).minusDays(1));
        FramtidigOppholdsInformasjon framtidig = new FramtidigOppholdsInformasjon(true, true,
                opphold(periode2, periode1));
        Set<ConstraintViolation<FramtidigOppholdsInformasjon>> constraintViolations = validator.validate(framtidig);
        System.out.println(constraintViolations);
        assertFalse(constraintViolations.isEmpty());
    }

    @Test
    public void testIkkeOverlappendeMenFramtid() {
        LukketPeriode periode1 = new LukketPeriode(now(), now().plusMonths(6));
        LukketPeriode periode2 = new LukketPeriode(now().plusMonths(6).plusDays(1), now().plusYears(1));
        TidligereOppholdsInformasjon framtidig = new TidligereOppholdsInformasjon(true, ARBEIDET_I_UTLANDET,
                opphold(periode2, periode1));
        Set<ConstraintViolation<TidligereOppholdsInformasjon>> constraintViolations = validator.validate(framtidig);
        assertFalse(constraintViolations.isEmpty());
    }

    @Test
    public void testOverlappendeFortid() {
        LukketPeriode periode1 = new LukketPeriode(now().minusMonths(6), now());
        LukketPeriode periode2 = new LukketPeriode(now().minusYears(1), now().minusMonths(4));
        LukketPeriode periode3 = new LukketPeriode(now().minusMonths(4), now());
        assertTrue(periode1.overlapper(periode2));
        assertTrue(periode2.overlapper(periode1));
        assertTrue(periode3.overlapper(periode1));
        TidligereOppholdsInformasjon tidligere = new TidligereOppholdsInformasjon(true, ARBEIDET_I_UTLANDET,
                opphold(periode1, periode2, periode3));
        // assertEquals(tidligere.getUtenlandsOpphold().size(), 3);
        assertFalse(validator.validate(tidligere).isEmpty());
    }

    private static LocalDate now() {
        return LocalDate.now();
    }

    private static List<Utenlandsopphold> opphold(LukketPeriode... perioder) {
        return Arrays.stream(perioder).map(s -> new Utenlandsopphold(CountryCode.SE, s)).collect(Collectors.toList());
    }
}