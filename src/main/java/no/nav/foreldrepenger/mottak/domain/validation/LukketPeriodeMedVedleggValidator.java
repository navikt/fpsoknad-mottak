package no.nav.foreldrepenger.mottak.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.LukketPeriode;

public class LukketPeriodeMedVedleggValidator implements ConstraintValidator<LukketPeriode, LukketPeriodeMedVedlegg> {

    @Override
    public boolean isValid(LukketPeriodeMedVedlegg periode, ConstraintValidatorContext context) {
        return periode.getFom() != null && periode.getTom() != null && periode.getFom().isBefore(periode.getTom());
    }
}
