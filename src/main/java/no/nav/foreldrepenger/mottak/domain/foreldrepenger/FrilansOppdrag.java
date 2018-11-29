package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FrilansOppdrag {
    @Length(max = 100)
    private final String oppdragsgiver;
    private final ÅpenPeriode periode;

    @JsonCreator
    public FrilansOppdrag(@JsonProperty("oppdragsgiver") String oppdragsgiver,
            @JsonProperty("periode") ÅpenPeriode periode) {
        this.oppdragsgiver = oppdragsgiver;
        this.periode = periode;
    }
}
