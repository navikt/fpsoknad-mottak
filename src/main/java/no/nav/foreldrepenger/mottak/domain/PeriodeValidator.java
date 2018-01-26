package no.nav.foreldrepenger.mottak.domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PeriodeValidator implements ConstraintValidator<Period, Periode> {

    @Override
    public boolean isValid(Periode value, ConstraintValidatorContext context) {
        return true;
    }

}
