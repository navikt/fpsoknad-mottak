package no.nav.foreldrepenger.mottak.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;

public class PeriodeTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testIkkeOverlappende() {
        LukketPeriode sisteHalvår = new LukketPeriode(LocalDate.now().minusMonths(6), LocalDate.now());
        LukketPeriode førsteHalvår = new LukketPeriode(LocalDate.now().minusYears(1),
                LocalDate.now().minusMonths(6).minusDays(1));
        assertFalse(sisteHalvår.overlapper(førsteHalvår));
        assertFalse(førsteHalvår.overlapper(sisteHalvår));
        TidligereOppholdsInformasjon tidligere = new TidligereOppholdsInformasjon(true,
                ArbeidsInformasjon.ARBEIDET_I_UTLANDET,
                Lists.newArrayList(new Utenlandsopphold(CountryCode.SE, sisteHalvår),
                        new Utenlandsopphold(CountryCode.FI, førsteHalvår)));
        System.out.println("Before " + tidligere);
        Set<ConstraintViolation<TidligereOppholdsInformasjon>> constraintViolations = validator.validate(tidligere);
        System.out.println(constraintViolations);
        assertTrue(constraintViolations.isEmpty());
        System.out.println("After " + tidligere);
    }

    @Test
    public void testOverlappende() {
        LukketPeriode periode1 = new LukketPeriode(LocalDate.now().minusMonths(6), LocalDate.now());
        LukketPeriode periode2 = new LukketPeriode(LocalDate.now().minusYears(1),
                LocalDate.now().minusMonths(4));
        assertTrue(periode1.overlapper(periode2));
        assertTrue(periode2.overlapper(periode1));
        TidligereOppholdsInformasjon tidligere = new TidligereOppholdsInformasjon(true,
                ArbeidsInformasjon.ARBEIDET_I_UTLANDET,
                Lists.newArrayList(new Utenlandsopphold(CountryCode.SE, periode2),
                        new Utenlandsopphold(CountryCode.FI, periode1)));
        Set<ConstraintViolation<TidligereOppholdsInformasjon>> constraintViolations = validator.validate(tidligere);
        System.out.println(constraintViolations);
        assertFalse(constraintViolations.isEmpty());
    }
}