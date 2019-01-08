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
@EqualsAndHashCode(callSuper = true, exclude = { "virksomhetsnummer" })
@ToString(callSuper = true, exclude = { "virksomhetsnummer" })
public class GradertUttaksPeriode extends UttaksPeriode {
    private final double arbeidstidProsent;
    private final boolean erArbeidstaker;
    private final List<String> virksomhetsnummer;
    private final boolean arbeidsForholdSomskalGraderes;

    @Builder
    @JsonCreator
    public GradertUttaksPeriode(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom,
            @JsonProperty("UttaksperiodeType") StønadskontoType uttaksperiodeType,
            @JsonProperty("ønskerSamtidigUttak") boolean ønskerSamtidigUttak,
            @JsonProperty("morsAktivitetsType") MorsAktivitet morsAktivitetsType,
            @JsonProperty("ønskerFlerbarnsdager") boolean ønskerFlerbarnsdager,
            @JsonProperty("samtidigUttakProsent") double samtidigUttakProsent,
            @JsonProperty("arbeidstidProsent") double arbeidstidProsent,
            @JsonProperty("erArbeidstaker") boolean erArbeidstaker,
            @JsonProperty("arbeidsForholdSomskalGraderes") boolean arbeidsForholdSomskalGraderes,
            @JsonProperty("virksomhetsnummer") List<String> virksomhetsnummer,
            @JsonProperty("vedlegg") List<String> vedlegg) {
        super(fom, tom, uttaksperiodeType, ønskerSamtidigUttak, morsAktivitetsType, ønskerFlerbarnsdager,
                samtidigUttakProsent, vedlegg);
        this.arbeidstidProsent = arbeidstidProsent;
        this.erArbeidstaker = erArbeidstaker;
        this.virksomhetsnummer = virksomhetsnummer;
        this.arbeidsForholdSomskalGraderes = arbeidsForholdSomskalGraderes;
    }
}
