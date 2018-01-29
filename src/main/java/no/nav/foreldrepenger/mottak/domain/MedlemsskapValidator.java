package no.nav.foreldrepenger.mottak.domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MedlemsskapValidator implements ConstraintValidator<Opphold, Medlemsskap> {

    @Override
    public boolean isValid(Medlemsskap medlemsskap, ConstraintValidatorContext context) {

        for (LukketPeriode periode : medlemsskap.getNorgesOpphold()) {

        }
        return true;
    }

}
