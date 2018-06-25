package no.nav.foreldrepenger.mottak.domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.validation.Periode;

public class PeriodeValidator implements ConstraintValidator<Periode, LukketPeriode> {

    @Override
    public boolean isValid(LukketPeriode periode, ConstraintValidatorContext context) {
        return periode.getFom().isBefore(periode.getTom());
    }

}
