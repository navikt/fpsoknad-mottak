package no.nav.foreldrepenger.mottak.domain.validation;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PastOrTodayValidator implements ConstraintValidator<PastOrToday, LocalDate> {

    private static final Logger LOG = LoggerFactory.getLogger(PastOrTodayValidator.class);

    @Override
    public boolean isValid(LocalDate dato, ConstraintValidatorContext context) {
        LOG.info("Sjekker {} mot  {}", dato, LocalDate.now());
        return dato.equals(LocalDate.now()) || dato.isBefore(LocalDate.now());
    }
}
