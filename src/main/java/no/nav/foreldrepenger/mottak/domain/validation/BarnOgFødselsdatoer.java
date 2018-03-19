package no.nav.foreldrepenger.mottak.domain.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { BarnOgFødselsdatoerValidator.class })
@Documented
public @interface BarnOgFødselsdatoer {

    String message() default "{ytelse.relasjontilbarn.fødsel.fødselsdato.match}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
