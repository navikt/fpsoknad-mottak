package no.nav.foreldrepenger.mottak.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Rettigheter;

public class RettigheterValidator implements ConstraintValidator<Rettighet, Rettigheter> {

    @Override
    public boolean isValid(Rettigheter rettigheter, ConstraintValidatorContext ctx) {
        if (!rettigheter.isHarAleneOmsorgForBarnet() && rettigheter.getDatoForAleneomsorg() != null) {
            return false;
        }
        return true;
    }
}
