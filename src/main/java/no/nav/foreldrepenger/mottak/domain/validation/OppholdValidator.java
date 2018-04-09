package no.nav.foreldrepenger.mottak.domain.validation;

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

    @Override
    public boolean isValid(List<Utenlandsopphold> alleOpphold, ConstraintValidatorContext context) {
        List<Pair<LukketPeriode, LukketPeriode>> overlappendePerioder = new ArrayList<>();
        if (alleOpphold.isEmpty()) {
            return true;
        }
        List<Utenlandsopphold> copy = new ArrayList<>(alleOpphold);
        while (!copy.isEmpty()) {
            Utenlandsopphold opphold = copy.remove(0);
            for (Utenlandsopphold o : copy) {
                LOG.info("Sammenligner {} og {}", opphold.getVarighet(), o.getVarighet());
                if (o.getVarighet().overlapper(opphold.getVarighet())) {
                    LOG.debug("Periodene overlapper");
                    overlappendePerioder.add(Pair.of(opphold.getVarighet(), o.getVarighet()));
                }
                else {
                    LOG.debug("Periodene overlapper ikke");

                }
            }
        }
        return overlappendePerioder.isEmpty();
    }

}
