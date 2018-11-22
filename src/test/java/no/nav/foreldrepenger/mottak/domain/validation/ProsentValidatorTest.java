package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.BeforeClass;
import org.junit.Test;

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

    @BeforeClass
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void test100Prosent() {
        assertStuff(true, 100d, 100d);
        assertStuff(true, "50%", 50d);
        assertStuff(true, 50d, 50d);
        assertStuff(-50d);
        assertStuff(true, 0d, 0d);
        assertStuff(100.1d);
    }

    private void assertStuff(double value) {
        assertStuff(false, value, value);

    }

    private void assertStuff(boolean shouldBeEmpty, String value, double expectedValue) {
        assertStuff(shouldBeEmpty, new ProsentBruker(ProsentAndel.valueOf(value)), expectedValue);
    }

    private void assertStuff(boolean shouldBeEmpty, double value, double expectedValue) {
        assertStuff(shouldBeEmpty, new ProsentBruker(new ProsentAndel(value)), expectedValue);
    }

    private static void assertStuff(boolean shouldBeEmpty, ProsentBruker prosent, double expectedValue) {
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