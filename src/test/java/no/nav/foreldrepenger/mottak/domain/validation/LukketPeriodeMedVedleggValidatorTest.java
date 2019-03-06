package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;

public class LukketPeriodeMedVedleggValidatorTest {

    private static Validator validator;

    @BeforeAll
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

    // @Test
    public void testStartLørdag() {
        LukketPeriodeMedVedlegg periode = new LukketPeriodeMedVedlegg(LocalDate.of(2019, Month.MARCH, 2),
                LocalDate.of(2019, Month.MARCH, 4), Collections.emptyList()) {
        };
        assertFalse(validator.validate(periode).isEmpty());
    }

    // @Test
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
