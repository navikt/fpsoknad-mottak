package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.LukketPeriode;

@Data
@EqualsAndHashCode(exclude = { "vedlegg" })
@LukketPeriode
@JsonPropertyOrder({ "fom", "tom" })
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = OverføringsPeriode.class, name = "overføring"),
        @Type(value = UttaksPeriode.class, name = "uttak"),
        @Type(value = OppholdsPeriode.class, name = "opphold"),
        @Type(value = UtsettelsesPeriode.class, name = "utsettelse")
})
public abstract class LukketPeriodeMedVedlegg {

    @NotNull
    protected final LocalDate fom;
    @NotNull
    protected final LocalDate tom;
    protected final List<String> vedlegg;

    @JsonCreator
    public LukketPeriodeMedVedlegg(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom,
            @JsonProperty("vedlegg") List<String> vedlegg) {
        this.fom = fom;
        this.tom = tom;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
    }

    @JsonIgnore
    public long dager() {
        return ChronoUnit.DAYS.between(fom, tom) + 1;
    }
}