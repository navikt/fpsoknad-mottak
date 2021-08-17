package no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true, exclude = { "morsAktivitetsType", "virksomhetsnummer" })
@EqualsAndHashCode(callSuper = true, exclude = { "morsAktivitetsType", "virksomhetsnummer" })
@JsonSubTypes({
        @Type(value = FriUtsettelsesPeriode.class, name = "fri")
})
public class UtsettelsesPeriode extends LukketPeriodeMedVedlegg {

    private final UtsettelsesÅrsak årsak;
    private final StønadskontoType uttaksperiodeType;
    private final boolean erArbeidstaker;
    private final List<String> virksomhetsnummer;
    private final MorsAktivitet morsAktivitetsType;

    public UtsettelsesPeriode(LocalDate fom, LocalDate tom, boolean erArbeidstaker, List<String> virksomhetsnummer,
            @NotNull UtsettelsesÅrsak årsak, @NotNull StønadskontoType uttaksperiodeType,
            MorsAktivitet morsAktivitetsType,
            List<String> vedlegg) {
        super(fom, tom, vedlegg);
        this.erArbeidstaker = erArbeidstaker;
        this.virksomhetsnummer = virksomhetsnummer;
        this.årsak = årsak;
        this.uttaksperiodeType = uttaksperiodeType;
        this.morsAktivitetsType = morsAktivitetsType;
    }
}
