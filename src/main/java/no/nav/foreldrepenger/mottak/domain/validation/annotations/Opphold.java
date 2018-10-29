package no.nav.foreldrepenger.mottak.domain.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import no.nav.foreldrepenger.mottak.domain.validation.OppholdValidator;

@Constraint(validatedBy = OppholdValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Opphold {

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    boolean fortid() default false;

    String message() default "Nei gi deg, da";
}
