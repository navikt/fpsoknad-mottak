package no.nav.foreldrepenger.mottak.domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TidligereOppholdsValidator implements ConstraintValidator<Opphold, TidligereOppholdsInformasjon> {

    @Override
    public boolean isValid(TidligereOppholdsInformasjon tidligereOpphold, ConstraintValidatorContext context) {
        if (tidligereOpphold.getArbeidsInfo().equals(ArbeidsInformasjon.IKKE_ARBEIDET)
                && !tidligereOpphold.getUtenlandsOpphold().isEmpty()) {
            return false;
        }
        if (tidligereOpphold.getArbeidsInfo().equals(ArbeidsInformasjon.ARBEIDET_I_NORGE)
                && !tidligereOpphold.getUtenlandsOpphold().isEmpty()) {
            return false;
        }
        if (tidligereOpphold.getArbeidsInfo().equals(ArbeidsInformasjon.ARBEIDET_I_UTLANDET)
                && tidligereOpphold.getUtenlandsOpphold().isEmpty()) {
            return false;
        }
        // TODO, sjekke overlappende eller inkomplette utenlandsperioder ?
        return true;
    }

}
