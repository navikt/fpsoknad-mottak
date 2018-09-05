package no.nav.foreldrepenger.mottak.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static java.time.LocalDate.now;

public class PastOrTodayValidator implements ConstraintValidator<PastOrToday, LocalDate> {

    private boolean nullable;

    @Override
    public boolean isValid(LocalDate dato, ConstraintValidatorContext context) {
        if (dato == null && nullable) {
            return true;
        }
        return dato != null && (dato.equals(now()) || dato.isBefore(now()));
    }

    @Override
    public void initialize(PastOrToday constraintAnnotation) {
        nullable = constraintAnnotation.nullable();
    }
}
