package no.nav.foreldrepenger.mottak.domain.validation;

import org.junit.BeforeClass;
import org.junit.Test;

import no.nav.foreldrepenger.mottak.domain.Søknad;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static no.nav.foreldrepenger.mottak.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SøknadValidatorTest {


    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @Test
    public void testSøknadMedFødselIDag() throws Exception {
        Set<ConstraintViolation<Søknad>> violations = validator.validate(engangssøknad(fødsel(nå())));
        assertThat(violations).isEmpty();
    }

}
