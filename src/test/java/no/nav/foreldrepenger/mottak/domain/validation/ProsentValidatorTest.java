package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;

public class ProsentValidatorTest {

    public class ProsentBruker {

        @Valid
        private final ProsentAndel prosent;

        public ProsentBruker(ProsentAndel prosent) {
            this.prosent = prosent;
        }

        public Double getProsent() {
            return prosent.getProsent();
        }

    }

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void test100() {
        testProsent(true, 100d);
    }

    @Test
    public void testOK() {
        testProsent(50d);
    }

    @Test
    public void testNegativ() {
        testProsent(false, -50d);
    }

    @Test
    public void test0() {
        testProsent(0d);
    }

    @Test
    public void testOver100() {
        testProsent(false, 100.1d);
    }

    private void testProsent(boolean shouldBeEmpty, double value) {
        testProsent(shouldBeEmpty, value, value);

    }

    private void testProsent(double value) {
        testProsent(true, value, value);

    }

    private void testProsent(boolean shouldBeEmpty, double value, double expectedValue) {
        testProsent(shouldBeEmpty, new ProsentBruker(new ProsentAndel(value)), expectedValue);
    }

    private static void testProsent(boolean shouldBeEmpty, ProsentBruker prosent, double expectedValue) {
        Set<ConstraintViolation<ProsentBruker>> constraintViolations = validator.validate(prosent);
        if (shouldBeEmpty) {
            assertTrue(constraintViolations.isEmpty());
            assertEquals(prosent.getProsent(), expectedValue, 0.1d);
        }
        else {
            assertFalse(constraintViolations.isEmpty());
        }

    }
}