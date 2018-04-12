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

import no.nav.foreldrepenger.mottak.domain.validation.Termindato;

public class TermindatoValidatorTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testiDag() {
        assertTrue(validator.validate(new TestClass(LocalDate.now())).isEmpty());
    }

    @Test
    public void testNærFortid() {
        assertTrue(validator.validate(new TestClass(LocalDate.now().minusDays(1))).isEmpty());
    }

    @Test
    public void testFjernFortid() {
        Set<ConstraintViolation<TestClass>> validate = validator
                .validate(new TestClass(LocalDate.now().minusWeeks(3).minusDays(1)));
        validate.forEach(s -> System.out.println(s));
        assertFalse(validate.isEmpty());
    }

    @Test
    public void testFjernFortid1() {
        assertTrue(validator.validate(new TestClass1(LocalDate.now().minusWeeks(3).minusDays(1))).isEmpty());
    }

    @Test
    public void testNull() {
        assertFalse(validator.validate(new TestClass(null)).isEmpty());
    }

    @Test
    public void testFramtid() {
        assertTrue(validator.validate(new TestClass(LocalDate.now().plusDays(1))).isEmpty());
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