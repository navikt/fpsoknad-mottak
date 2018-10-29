package no.nav.foreldrepenger.mottak.domain.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import no.nav.foreldrepenger.mottak.domain.validation.LukketPeriodeMedVedleggValidator;

@Constraint(validatedBy = LukketPeriodeMedVedleggValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface LukketPeriode {

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String message() default "{ytelse.medlemsskap.periode.ugyldig}";
}
