package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GradertUttaksPeriode extends UttaksPeriode {
    private double arbeidstidProsent;
    private boolean erArbeidstaker;
    private String virksomhetsNummer;
    private boolean arbeidsForholdSomskalGraderes;

    @Builder
    @JsonCreator
    public GradertUttaksPeriode(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom,
            @JsonProperty("vedlegg") List<String> vedlegg,
            @JsonProperty("UttaksperiodeType") StønadskontoType uttaksperiodeType,
            @JsonProperty("ønskerSamtidigUttak") boolean ønskerSamtidigUttak,
            @JsonProperty("morsAktivitetsType") MorsAktivitet morsAktivitetsType) {
        super(fom, tom, uttaksperiodeType, ønskerSamtidigUttak, morsAktivitetsType, vedlegg);

    }
}
