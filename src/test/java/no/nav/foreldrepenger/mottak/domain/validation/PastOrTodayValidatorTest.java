package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.validation.annotations.PastOrToday;

public class PastOrTodayValidatorTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testiDag() {
        assertTrue(validator.validate(new TestClass(LocalDate.now())).isEmpty());
    }

    @Test
    public void testFortid() {
        assertTrue(validator.validate(new TestClass(LocalDate.now().minusDays(1))).isEmpty());
    }

    @Test
    public void testNull() {
        assertFalse(validator.validate(new TestClass(null)).isEmpty());
    }

    @Test
    public void testFramtid() {
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