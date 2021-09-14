package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.domain.validation.annotations.Termindato;

class TermindatoValidatorTest {

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
    void testNærFortid() {
        assertTrue(validator.validate(new TestClass(LocalDate.now().minusDays(1))).isEmpty());
    }

    @Test
    void testFjernFortid() {
        assertFalse(validator
                .validate(new TestClass(LocalDate.now().minusWeeks(3).minusDays(1))).isEmpty());
    }

    @Test
    void testFjernFortid1() {
        assertTrue(validator.validate(new TestClass1(LocalDate.now().minusWeeks(3).minusDays(1))).isEmpty());
    }

    @Test
    void testNull() {
        assertFalse(validator.validate(new TestClass(null)).isEmpty());
    }

    @Test
    void testFramtid() {
        assertTrue(validator.validate(new TestClass(LocalDate.now().plusDays(1))).isEmpty());
    }

    @Test
    void testAkkuratTreUkerFortid() {
        assertTrue(validator.validate(new TestClass(LocalDate.now().minusWeeks(3))).isEmpty());
    }

    private static class TestClass {

        @Termindato
        private final LocalDate dato;

        public TestClass(LocalDate dato) {
            this.dato = dato;
        }

    }

    private static class TestClass1 {

        @Termindato(weeks = 8)
        private final LocalDate dato;

        public TestClass1(LocalDate dato) {
            this.dato = dato;
        }

    }
}
