package no.nav.foreldrepenger.mottak.domain.validation;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PastOrTodayValidator implements ConstraintValidator<PastOrToday, LocalDate> {

    @Override
    public boolean isValid(LocalDate dato, ConstraintValidatorContext context) {
        return dato.equals(LocalDate.now()) || dato.isBefore(LocalDate.now());
    }
}
