package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.domain.validation.annotations.PastOrToday;

class PastOrTodayValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testiDag() {
        assertTrue(validator.validate(new TestClass(LocalDate.now())).isEmpty());
    }

    @Test
    void testFortid() {
        assertTrue(validator.validate(new TestClass(LocalDate.now().minusDays(1))).isEmpty());
    }

    @Test
    void testNull() {
        assertFalse(validator.validate(new TestClass(null)).isEmpty());
    }

    @Test
    void testFramtid() {
        assertFalse(validator.validate(new TestClass(LocalDate.now().plusDays(1))).isEmpty());
    }

    static class TestClass {

        @PastOrToday
        private final LocalDate dato;

        public TestClass(LocalDate dato) {
            this.dato = dato;
        }

    }
}
