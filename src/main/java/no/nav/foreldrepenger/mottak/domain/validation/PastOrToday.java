package no.nav.foreldrepenger.mottak.domain.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PastOrTodayValidator.class)
@Documented
public @interface PastOrToday {
    String message()

    default "{ytelse.relasjontilbarn.framtidigfødsel.terminbekreftelse.fortid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
