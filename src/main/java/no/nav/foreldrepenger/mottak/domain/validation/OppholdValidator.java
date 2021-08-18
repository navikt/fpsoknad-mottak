package no.nav.foreldrepenger.mottak.domain.validation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Opphold;

public class OppholdValidator implements ConstraintValidator<Opphold, List<Utenlandsopphold>> {

    private static final Logger LOG = LoggerFactory.getLogger(OppholdValidator.class);

    private boolean fortid;

    @Override
    public void initialize(Opphold constraintAnnotation) {
        this.fortid = constraintAnnotation.fortid();
    }

    @Override
    public boolean isValid(List<Utenlandsopphold> alleOpphold, ConstraintValidatorContext context) {

        boolean valid = true;
        List<Utenlandsopphold> copy = new ArrayList<>(alleOpphold);
        while (!copy.isEmpty()) {
            Utenlandsopphold opphold = copy.remove(0);
            for (Utenlandsopphold o : copy) {
                if (validerFortid(opphold)) {
                    LOG.debug("Periode {} er ikke utelukkende i fortiden", opphold);
                    errorMessageFortidFremtid(context, opphold, "er ikke utelukkende i fortiden");
                    valid = false;
                }
                if (validerFramtid(opphold)) {
                    LOG.debug("Periode {} er ikke i utelukkende framtiden", opphold);
                    errorMessageFortidFremtid(context, opphold, "er ikke i utelukkende framtiden");
                    valid = false;
                }
                LOG.debug("Sammenligner {} og {}", opphold.varighet(), o.varighet());
                if (overlapper(o.varighet(), opphold.varighet())) {
                    LOG.debug("Periodene overlapper");
                    errorMessageOverlap(context, opphold, o);
                    valid = false;
                } else {
                    LOG.info("Periode {} validert OK", opphold);
                }
            }
        }
        return valid;
    }

    private static boolean overlapper(LukketPeriode førstePeriode, LukketPeriode annenPeriode) {
        LOG.info("Sammeligner {} med {}", førstePeriode, annenPeriode);
        if (annenPeriode.fom().isAfter(førstePeriode.tom())) {
            LOG.info("Periodene overlapper ikke");
            return false;
        }
        if (annenPeriode.tom().isBefore(førstePeriode.fom())) {
            LOG.info("Periodene overlapper ikke");
            return false;
        }
        LOG.info("Periodene overlapper");
        return true;
    }

    private static void errorMessageFortidFremtid(ConstraintValidatorContext context, Utenlandsopphold opphold,
            String txt) {
        HibernateConstraintValidatorContext hibernateContext = context
                .unwrap(HibernateConstraintValidatorContext.class);
        hibernateContext.disableDefaultConstraintViolation();
        hibernateContext.addExpressionVariable("fra", opphold.fom())
                .addExpressionVariable("til", opphold.tom())
                .addExpressionVariable("txt", txt)
                .buildConstraintViolationWithTemplate("Perioden ${fra} - ${til} ${txt}")
                .addConstraintViolation();
    }

    private static void errorMessageOverlap(ConstraintValidatorContext context, Utenlandsopphold periode1,
            Utenlandsopphold periode2) {
        HibernateConstraintValidatorContext hibernateContext = context
                .unwrap(HibernateConstraintValidatorContext.class);
        hibernateContext.disableDefaultConstraintViolation();
        hibernateContext
                .addExpressionVariable("fra1", periode1.fom())
                .addExpressionVariable("til1", periode1.tom())
                .addExpressionVariable("fra2", periode2.fom())
                .addExpressionVariable("til2", periode2.tom())
                .buildConstraintViolationWithTemplate("Periodene ${fra1} - ${til1} og ${fra2} - ${til2} overlpper")
                .addConstraintViolation();
    }

    private boolean validerFramtid(Utenlandsopphold opphold) {
        return !fortid && isBeforeNow(opphold);
    }

    private boolean validerFortid(Utenlandsopphold opphold) {
        return fortid && isAfterNow(opphold);
    }

    private static boolean isAfterNow(Utenlandsopphold opphold) {
        return opphold.fom().isAfter(LocalDate.now()) ||
                opphold.tom().isAfter(LocalDate.now());
    }

    private static boolean isBeforeNow(Utenlandsopphold opphold) {
        return opphold.fom().isBefore(LocalDate.now()) ||
                opphold.tom().isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fortid=" + fortid + "]";
    }
}
