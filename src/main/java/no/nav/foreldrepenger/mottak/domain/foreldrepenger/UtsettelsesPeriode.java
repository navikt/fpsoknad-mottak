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
    private final MorsAktivitet morsAktivitetsType;

    public UtsettelsesPeriode(LocalDate fom, LocalDate tom, boolean erArbeidstaker, String virksomhetsnummer,
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        UtsettelsesPeriode other = (UtsettelsesPeriode) obj;
        if (erArbeidstaker != other.erArbeidstaker)
            return false;

        if (uttaksperiodeType != other.uttaksperiodeType)
            return false;
        if (virksomhetsnummer == null) {
            if (other.virksomhetsnummer != null)
                return false;
        }
        else if (!virksomhetsnummer.equals(other.virksomhetsnummer))
            return false;
        if (årsak != other.årsak)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (erArbeidstaker ? 1231 : 1237);
        result = prime * result + ((uttaksperiodeType == null) ? 0 : uttaksperiodeType.hashCode());
        result = prime * result + ((virksomhetsnummer == null) ? 0 : virksomhetsnummer.hashCode());
        result = prime * result + ((årsak == null) ? 0 : årsak.hashCode());
        return result;
    }
}
