package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "morsAktivitetsType", "virksomhetsnummer" })
@ToString(callSuper = true)
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
