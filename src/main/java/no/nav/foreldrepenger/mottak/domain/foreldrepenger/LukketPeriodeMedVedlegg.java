package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.Periode;

@Data
@Periode
@JsonPropertyOrder({ "fom", "tom" })
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = OverføringsPeriode.class, name = "overføring"),
        @Type(value = UttaksPeriode.class, name = "uttak"),
        @Type(value = OppholdsPeriode.class, name = "opphold"),
        @Type(value = UtsettelsesPeriode.class, name = "utsettelse")
})
public abstract class LukketPeriodeMedVedlegg {

    private final LocalDate fom;
    private final LocalDate tom;
    private final List<String> vedlegg;

    @JsonCreator
    public LukketPeriodeMedVedlegg(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom,
            @JsonProperty("vedlegg") List<String> vedlegg) {
        this.fom = fom;
        this.tom = tom;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
    }
}