package no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Fordeling {

    private final boolean erAnnenForelderInformert;
    private final Overføringsårsak ønskerKvoteOverført;
    @Valid
    private final List<LukketPeriodeMedVedlegg> perioder;

    @JsonCreator
    public Fordeling(@JsonProperty("erAnnenForelderInformert") boolean erAnnenForelderInformert,
            @JsonProperty("årsak") Overføringsårsak årsak,
            @JsonProperty("perioder") List<LukketPeriodeMedVedlegg> perioder) {
        this.erAnnenForelderInformert = erAnnenForelderInformert;
        this.ønskerKvoteOverført = årsak;
        this.perioder = Optional.ofNullable(perioder).orElse(emptyList()).stream().sorted().collect(toList());
    }

    @JsonIgnore
    public LocalDate getFørsteUttaksdag() {
        for (LukketPeriodeMedVedlegg periode : perioder) {
            if (periode instanceof UttaksPeriode || periode instanceof UtsettelsesPeriode) {
                return periode.getFom();
            }
        }
        return null;
    }
}