package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.BeforeClass;
import org.junit.Test;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.LukketPeriodeMedVedlegg;

public class LukketPeriodeMedVedleggValidatorTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testOKEnDag() {
        LukketPeriodeMedVedlegg periode = new LukketPeriodeMedVedlegg(LocalDate.of(2019, Month.MARCH, 1),
                LocalDate.of(2019, Month.MARCH, 1), Collections.emptyList()) {
        };
        assertTrue(validator.validate(periode).isEmpty());
    }

    @Test
    public void testOKOverHelgen() {
        LukketPeriodeMedVedlegg periode = new LukketPeriodeMedVedlegg(LocalDate.of(2019, Month.MARCH, 1),
                LocalDate.of(2019, Month.MARCH, 4), Collections.emptyList()) {
        };
        assertTrue(validator.validate(periode).isEmpty());
    }

    //@Test
    public void testStartLørdag() {
        LukketPeriodeMedVedlegg periode = new LukketPeriodeMedVedlegg(LocalDate.of(2019, Month.MARCH, 2),
                LocalDate.of(2019, Month.MARCH, 4), Collections.emptyList()) {
        };
        assertFalse(validator.validate(periode).isEmpty());
    }

    //@Test
    public void testSluttSøndag() {
        LukketPeriodeMedVedlegg periode = new LukketPeriodeMedVedlegg(LocalDate.of(2019, Month.MARCH, 1),
                LocalDate.of(2019, Month.MARCH, 3), Collections.emptyList()) {
        };
        assertFalse(validator.validate(periode).isEmpty());
    }

    @Test
    public void testNullStart() {
        LukketPeriodeMedVedlegg periode = new LukketPeriodeMedVedlegg(null,
                LocalDate.of(2019, Month.MARCH, 3), Collections.emptyList()) {
        };
        assertFalse(validator.validate(periode).isEmpty());
    }

    @Test
    public void testNullEnd() {
        LukketPeriodeMedVedlegg periode = new LukketPeriodeMedVedlegg(LocalDate.of(2019, Month.MARCH, 3), null,
                Collections.emptyList()) {
        };
        assertFalse(validator.validate(periode).isEmpty());
    }

}
