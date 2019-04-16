package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.validation.annotations.Prosent;

public class ProsentValidatorTest {

    private static class ProsentBruker {

        @Prosent
        private final Double prosent;
        @Prosent(max = 200, min = -10)
        private final Double prosent1;

        ProsentBruker(Double prosent, Double prosent1) {
            this.prosent = prosent;
            this.prosent1 = prosent1;
        }
    }

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testMax() {
        test(true, 100);
    }

    @Test
    public void testMax2() {
        test(true, 100, 200);
    }

    @Test
    public void testOverMax() {
        test(false, 100, 201);
    }

    @Test
    public void testUnderMin() {
        test(false, -50);
    }

    @Test
    public void testUnderMin2() {
        test(false, -2, -20);
    }

    @Test
    public void testMin() {
        test(0);
    }

    @Test
    public void testMin2() {
        test(true, 0, -10);
    }

    private static void test(Number value) {
        test(true, value);
    }

    private static void test(boolean empty, Number value) {
        test(empty, value, value);
    }

    private static void test(boolean empty, Number value, Number value1) {
        test(empty, new ProsentBruker(value.doubleValue(), value1.doubleValue()));
    }

    private static void test(boolean empty, ProsentBruker prosent) {
        assertEquals(empty, VALIDATOR.validate(prosent).isEmpty());
    }
}
