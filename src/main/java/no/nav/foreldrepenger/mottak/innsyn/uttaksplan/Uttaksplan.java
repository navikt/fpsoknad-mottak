package no.nav.foreldrepenger.mottak.innsyn.uttaksplan;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Uttaksplan {

    private final SøknadsGrunnlag grunnlag;
    private final List<UttaksPeriode> perioder;

    @JsonCreator
    public Uttaksplan(@JsonProperty("grunnlag") SøknadsGrunnlag grunnlag,
            @JsonProperty("perioder") List<UttaksPeriode> perioder) {
        this.grunnlag = grunnlag;
        this.perioder = safeStream(perioder).sorted().collect(toList());
    }
}
