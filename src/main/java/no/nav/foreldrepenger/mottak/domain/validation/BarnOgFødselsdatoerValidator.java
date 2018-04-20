package no.nav.foreldrepenger.mottak.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import no.nav.foreldrepenger.mottak.domain.Fødsel;

public class BarnOgFødselsdatoerValidator implements ConstraintValidator<BarnOgFødselsdatoer, Fødsel> {

    @Override
    public boolean isValid(Fødsel fødsel, ConstraintValidatorContext ctx) {
        return fødsel.getFødselsdato().size() == 1;
    }
}
