package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.domain.validation.annotations.Orgnr;

class OrgnrValidatorTest {

    private static final String NAV = "999263550";
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testOK() {
        assertTrue(validator.validate(new TestClass(NAV)).isEmpty());
    }

    @Test
    void testOKbutWrongFirstDigit() {
        assertFalse(validator.validate(new TestClass("123456785")).isEmpty());
    }

    @Test
    void testStrange() {
        assertTrue(validator.validate(new TestClass("999999999")).isEmpty());
    }

    @Test
    void testNull() {
        assertTrue(validator.validate(new TestClass(null)).isEmpty());
    }

    @Test
    void testLength() {
        assertFalse(validator.validate(new TestClass("666")).isEmpty());
    }

    static class TestClass {

        @Orgnr
        private final String orgnr;

        public TestClass(String orgnr) {
            this.orgnr = orgnr;
        }

    }
}
