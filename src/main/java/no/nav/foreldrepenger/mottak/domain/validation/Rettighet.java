package no.nav.foreldrepenger.mottak.domain.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Constraint(validatedBy = RettigheterValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Rettighet {

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String message() default "{ytelse.rettighet.ugyldig}";
}
