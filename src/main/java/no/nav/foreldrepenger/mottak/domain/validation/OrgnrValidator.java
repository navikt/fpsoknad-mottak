package no.nav.foreldrepenger.mottak.domain.validation;

import static no.nav.foreldrepenger.mottak.domain.Orgnummer.MAGIC;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import no.nav.foreldrepenger.mottak.domain.validation.annotations.Orgnr;

public class OrgnrValidator implements ConstraintValidator<Orgnr, String> {

    @Override
    public boolean isValid(String orgnr, ConstraintValidatorContext context) {
        if (orgnr == null) {
            return true;
        }

        if (orgnr.length() != 9) {
            return false;
        }

        if (orgnr.equals(MAGIC)) {
            return true;
        }
        if (!(orgnr.startsWith("8") || orgnr.startsWith("9"))) {
            return false;
        }

        int value = mod11OfNumberWithControlDigit(orgnr.substring(0, 8));
        return orgnr.charAt(8) - 48 == value;
    }

    private static int mod11OfNumberWithControlDigit(String orgnr) {
        int[] weights = new int[] { 3, 2, 7, 6, 5, 4, 3, 2 };
        int sumForMod = 0;
        for (int i = 0; i < orgnr.length(); i++) {
            sumForMod += (orgnr.charAt(i) - 48) * weights[i];
        }
        int result = 11 - sumForMod % 11;
        return result == 11 ? 0 : result;
    }
}
