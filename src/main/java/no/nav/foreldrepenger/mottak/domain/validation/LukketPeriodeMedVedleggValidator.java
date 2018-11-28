package no.nav.foreldrepenger.mottak.domain.validation;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.LukketPeriode;

public class LukketPeriodeMedVedleggValidator implements ConstraintValidator<LukketPeriode, LukketPeriodeMedVedlegg> {

    private static final Logger LOG = LoggerFactory.getLogger(LukketPeriodeMedVedleggValidator.class);

    @Override
    public boolean isValid(LukketPeriodeMedVedlegg periode, ConstraintValidatorContext context) {
        LOG.trace("Validerer periode {} ({}-{})", periode.getClass().getSimpleName(), periode.getFom(),
                periode.getTom());
        if (periode.getFom() == null) {
            return false;
        }
        if (periode.getTom() == null) {
            return false;
        }
        if (!erUkedag(periode.getFom())) {
            return false;
        }
        if (!erUkedag(periode.getTom())) {
            return false;
        }
        if (periode.getFom().isAfter(periode.getTom())) {
            return false;
        }
        return true;
    }

    private static boolean erUkedag(LocalDate dato) {
        boolean status = !dato.getDayOfWeek().equals(SATURDAY) && !dato.getDayOfWeek().equals(SUNDAY);
        if (!status) {
            LOG.warn("{} er IKKE en ukedag", dato);
        }
        return status;
    }
}
