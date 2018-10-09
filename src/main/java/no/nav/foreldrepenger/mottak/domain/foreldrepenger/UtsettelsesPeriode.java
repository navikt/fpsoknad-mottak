package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UtsettelsesPeriode extends LukketPeriodeMedVedlegg {

    private final UtsettelsesÅrsak årsak;
    private final StønadskontoType uttaksperiodeType;
    private final boolean erArbeidstaker;
    private final String virksomhetsnummer;

    public UtsettelsesPeriode(LocalDate fom, LocalDate tom, boolean erArbeidstaker, String virksomhetsnummer,
            UtsettelsesÅrsak årsak, @NotNull StønadskontoType uttaksperiodeType,
            List<String> vedlegg) {
        super(fom, tom, vedlegg);
        this.erArbeidstaker = erArbeidstaker;
        this.virksomhetsnummer = virksomhetsnummer;
        this.årsak = årsak;
        this.uttaksperiodeType = uttaksperiodeType;
    }
}
