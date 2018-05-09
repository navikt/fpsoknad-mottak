package no.nav.foreldrepenger.mottak.domain.validation;

import static no.nav.foreldrepenger.mottak.domain.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.nå;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.BeforeClass;
import org.junit.Test;

import no.nav.foreldrepenger.mottak.domain.Søknad;

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
