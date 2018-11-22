package no.nav.foreldrepenger.mottak.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import no.nav.foreldrepenger.mottak.domain.validation.annotations.Prosent;

public class ProsentValidator implements ConstraintValidator<Prosent, Double> {

    @Override
    public boolean isValid(Double prosent, ConstraintValidatorContext context) {
        return !(prosent == null || prosent.doubleValue() < 0D || prosent.doubleValue() > 100D);
    }
}
