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
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Prosent;

public class ProsentValidatorTest {

    public class ProsentBruker {

        @Valid
        @Prosent
        private final Double prosent;
        @Prosent(max = 200)
        private final Double prosent1;

        public ProsentBruker(Double prosent) {
            this(prosent, prosent);
        }

        public ProsentBruker(Double prosent, Double prosent1) {
            this.prosent = prosent;
            this.prosent1 = prosent1;
        }
    }

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testMax() {
        testProsent(true, 100);
    }

    @Test
    public void testMax2() {
        testProsent(true, 100, 200);
    }

    @Test
    public void testOverMax() {
        testProsent(false, 100, 201);
    }

    @Test
    public void testUnderMin() {
        testProsent(false, -50);
    }

    @Test
    public void testMin() {
        testProsent(0);
    }

    private void testProsent(Number value) {
        testProsent(true, value);

    }

    private void testProsent(boolean shouldBeEmpty, Number value) {
        testProsent(shouldBeEmpty, value, value);
    }

    private void testProsent(boolean shouldBeEmpty, Number value, Number value1) {
        testProsent(shouldBeEmpty, new ProsentBruker(value.doubleValue(), value1.doubleValue()));
    }

    private static void testProsent(boolean shouldBeEmpty, ProsentBruker prosent) {
        assertEquals(shouldBeEmpty, validator.validate(prosent).isEmpty());
    }
}
