package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Fordeling {

    private final boolean erAnnenForelderInformert;
    private final Overføringsårsak ønskerKvoteOverført;
    private final List<LukketPeriodeMedVedlegg> perioder;

    @JsonCreator
    public Fordeling(@JsonProperty("erAnnenForelderInformert") boolean erAnnenForelderInformert,
            @JsonProperty("årsak") Overføringsårsak årsak,
            @JsonProperty("perioder") List<LukketPeriodeMedVedlegg> perioder) {
        this.erAnnenForelderInformert = erAnnenForelderInformert;
        this.ønskerKvoteOverført = årsak;
        this.perioder = Optional.ofNullable(perioder).orElse(emptyList());
    }
}
