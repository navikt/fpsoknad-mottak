package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = GradertUttaksPeriode.class, name = "gradert")
})
public class UttaksPeriode extends LukketPeriodeMedVedlegg {
    private final UttaksperiodeType uttaksperiodeType;
    private final boolean ønskerSamtidigUttak;
    private final MorsAktivitetstype morsAktivitetsType;

    @JsonCreator
    public UttaksPeriode(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom,
            @JsonProperty("vedlegg") List<String> vedlegg,
            @JsonProperty("UttaksperiodeType") UttaksperiodeType uttaksperiodeType,
            @JsonProperty("ønskerSamtidigUttak") boolean ønskerSamtidigUttak,
            @JsonProperty("morsAktivitetsType") MorsAktivitetstype morsAktivitetsType) {
        super(fom, tom, vedlegg != null ? vedlegg : Collections.emptyList());
        this.uttaksperiodeType = uttaksperiodeType;
        this.ønskerSamtidigUttak = ønskerSamtidigUttak;
        this.morsAktivitetsType = morsAktivitetsType;
    }
}
