package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.OppholdsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Oppholdsårsak;

class LukketPeriodeMedVedleggValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testOKEnDag() {
        var periode = new OppholdsPeriode(LocalDate.of(2019, Month.MARCH, 1),
                LocalDate.of(2019, Month.MARCH, 1), Oppholdsårsak.INGEN, List.of());
        assertTrue(validator.validate(periode).isEmpty());
    }

    @Test
    void testNullStart() {
        var periode = new OppholdsPeriode(null,
                LocalDate.of(2019, Month.MARCH, 3), Oppholdsårsak.INGEN, List.of());
        assertFalse(validator.validate(periode).isEmpty());
    }

    @Test
    void testNullEnd() {
        var periode = new OppholdsPeriode(LocalDate.of(2019, Month.MARCH, 3), null, Oppholdsårsak.INGEN,
                List.of());
        assertFalse(validator.validate(periode).isEmpty());
    }

}
