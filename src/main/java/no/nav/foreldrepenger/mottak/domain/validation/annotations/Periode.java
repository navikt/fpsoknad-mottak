package no.nav.foreldrepenger.mottak.domain.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import no.nav.foreldrepenger.mottak.domain.validation.PeriodeValidator;

@Constraint(validatedBy = PeriodeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Periode {

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String message() default "{ytelse.medlemsskap.periode.ugyldig}";
}
