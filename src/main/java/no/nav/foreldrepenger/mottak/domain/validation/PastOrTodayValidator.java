package no.nav.foreldrepenger.mottak.domain.validation;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PastOrTodayValidator implements ConstraintValidator<PastOrToday, LocalDate> {

    private boolean nullable;

    @Override
    public boolean isValid(LocalDate dato, ConstraintValidatorContext context) {
        if (dato == null && nullable) {
            return true;
        }
        return dato.equals(LocalDate.now()) || dato.isBefore(LocalDate.now());
    }

    @Override
    public void initialize(PastOrToday constraintAnnotation) {
        nullable = constraintAnnotation.nullable();
    }
}
