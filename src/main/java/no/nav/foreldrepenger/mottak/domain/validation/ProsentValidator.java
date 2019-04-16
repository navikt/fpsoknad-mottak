package no.nav.foreldrepenger.mottak.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import no.nav.foreldrepenger.mottak.domain.validation.annotations.Prosent;

public class ProsentValidator implements ConstraintValidator<Prosent, Double> {

    private double min;
    private double max;

    @Override
    public void initialize(Prosent constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Double prosent, ConstraintValidatorContext context) {
        return !(prosent == null || prosent.doubleValue() < min || prosent.doubleValue() > max);
    }
}
