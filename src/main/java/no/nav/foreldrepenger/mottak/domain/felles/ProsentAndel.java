package no.nav.foreldrepenger.mottak.domain.felles;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Prosent;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;

@Data
public class ProsentAndel {

    @Prosent
    private final Double prosent;

    @JsonCreator
    public ProsentAndel(@JsonProperty("prosent") Object prosent) {
        this.prosent = prosentFra(prosent);
    }

    private static Double prosentFra(Object prosent) {
        if (prosent instanceof Double) {
            return round(Double.class.cast(prosent), 1);
        }
        if (prosent instanceof Integer) {
            return Integer.class.cast(prosent).doubleValue();
        }
        throw new UnexpectedInputException("Ukjent prosent klasse %s", prosent.getClass().getSimpleName());
    }

    private static double round(Double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
