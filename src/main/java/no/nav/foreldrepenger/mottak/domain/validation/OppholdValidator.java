package no.nav.foreldrepenger.mottak.domain.validation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.domain.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.Pair;
import no.nav.foreldrepenger.mottak.domain.Utenlandsopphold;

public class OppholdValidator implements ConstraintValidator<Opphold, List<Utenlandsopphold>> {

    private static final Logger LOG = LoggerFactory.getLogger(OppholdValidator.class);

    private boolean fortid;

    @Override
    public void initialize(Opphold constraintAnnotation) {
        this.fortid = constraintAnnotation.fortid();
    }

    @Override
    public boolean isValid(List<Utenlandsopphold> alleOpphold, ConstraintValidatorContext context) {

        List<Pair<LukketPeriode, LukketPeriode>> ikkeValidertePerioder = new ArrayList<>();
        if (alleOpphold.isEmpty()) {
            return true;
        }
        List<Utenlandsopphold> copy = new ArrayList<>(alleOpphold);
        while (!copy.isEmpty()) {
            Utenlandsopphold opphold = copy.remove(0);
            for (Utenlandsopphold o : copy) {
                if (validerFortid(opphold)) {
                    LOG.debug("Periode {} er ikke utelukkende i fortiden", opphold);
                    errorMessage(context);
                    ikkeValidertePerioder.add(Pair.of(opphold.getVarighet(), o.getVarighet()));
                }
                if (validerFramtid(opphold)) {
                    LOG.debug("Periode {} er ikke i utelukkende framtiden", opphold);
                    errorMessage(context);
                    ikkeValidertePerioder.add(Pair.of(opphold.getVarighet(), o.getVarighet()));
                }
                LOG.info("Sammenligner {} og {}", opphold.getVarighet(), o.getVarighet());
                if (o.getVarighet().overlapper(opphold.getVarighet())) {
                    LOG.debug("Periodene overlapper");
                    errorMessage(context);
                    ikkeValidertePerioder.add(Pair.of(opphold.getVarighet(), o.getVarighet()));
                }
                else {
                    LOG.debug("Periode {} validert OK", opphold);
                }
            }
        }
        return ikkeValidertePerioder.isEmpty();
    }

    private void errorMessage(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "{ytelse.medlemsskap.periode.ugyldig}")
                .addPropertyNode("varighet")
                .addConstraintViolation();
    }

    private boolean validerFramtid(Utenlandsopphold opphold) {
        return !fortid && isBeforeNow(opphold);
    }

    private boolean validerFortid(Utenlandsopphold opphold) {
        return fortid && isAfterNow(opphold);
    }

    private static boolean isAfterNow(Utenlandsopphold opphold) {
        return opphold.getVarighet().getFom().isAfter(LocalDate.now()) ||
                opphold.getVarighet().getTom().isAfter((LocalDate.now()));
    }

    private static boolean isBeforeNow(Utenlandsopphold opphold) {
        return opphold.getVarighet().getFom().isBefore(LocalDate.now()) ||
                opphold.getVarighet().getTom().isBefore((LocalDate.now()));
    }
}
