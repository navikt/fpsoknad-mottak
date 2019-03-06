package no.nav.foreldrepenger.mottak.domain.validation;

import no.nav.foreldrepenger.mottak.domain.felles.Fødsel;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.BarnOgFødselsdatoer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BarnOgFødselsdatoerValidator implements ConstraintValidator<BarnOgFødselsdatoer, Fødsel> {

    @Override
    public boolean isValid(Fødsel fødsel, ConstraintValidatorContext ctx) {
        return fødsel.getFødselsdato().size() == 1;
    }
}
