package no.nav.foreldrepenger.mottak.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
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

        if (periode.getFom().isAfter(periode.getTom())) {
            return false;
        }
        return true;
    }
}
