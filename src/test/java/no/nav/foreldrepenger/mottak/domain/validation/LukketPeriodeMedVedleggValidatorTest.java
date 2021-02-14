package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;

class LukketPeriodeMedVedleggValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testOKEnDag() {
        var periode = new LukketPeriodeMedVedlegg(LocalDate.of(2019, Month.MARCH, 1),
                LocalDate.of(2019, Month.MARCH, 1), Collections.emptyList()) {
        };
        assertTrue(validator.validate(periode).isEmpty());
    }

    @Test
    void testNullStart() {
        var periode = new LukketPeriodeMedVedlegg(null,
                LocalDate.of(2019, Month.MARCH, 3), Collections.emptyList()) {
        };
        assertFalse(validator.validate(periode).isEmpty());
    }

    @Test
    void testNullEnd() {
        var periode = new LukketPeriodeMedVedlegg(LocalDate.of(2019, Month.MARCH, 3), null,
                List.of()) {
        };
        assertFalse(validator.validate(periode).isEmpty());
    }

}
