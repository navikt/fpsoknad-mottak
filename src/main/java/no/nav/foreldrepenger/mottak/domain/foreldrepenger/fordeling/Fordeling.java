package no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
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
        this.perioder = safeStream(perioder).sorted().collect(toList());
    }
}