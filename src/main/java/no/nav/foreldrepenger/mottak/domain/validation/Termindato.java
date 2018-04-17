package no.nav.foreldrepenger.mottak.domain.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;

@NotNull
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TermindatoValidator.class)
@Documented
@ReportAsSingleViolation
public @interface Termindato {
    String message() default "{ytelse.relasjontilbarn.framtidig.termindato.fortid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int weeks() default 3;
}
