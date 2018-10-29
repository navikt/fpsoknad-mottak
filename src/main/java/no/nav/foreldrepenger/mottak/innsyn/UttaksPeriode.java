package no.nav.foreldrepenger.mottak.innsyn;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;

@Data
public class UttaksPeriode {

    private final PerioderResultatType periodeResultatType;
    private final Boolean graderingInnvilget;
    private final Boolean samtidigUttak;
    private final LukketPeriode periode;
    private final Trekkonto trekkonto;
    private final Integer trekkDager;
    private final Double arbeidstidProsent;
    private final Double utbetalingprosent;
    private final Boolean gjelderAnnenPart;

    public UttaksPeriode(@JsonProperty("periodeResultatType") PerioderResultatType periodeResultatType,
            @JsonProperty("graderingInnvilget") Boolean graderingInnvilget,
            @JsonProperty("samtidigUttak") Boolean samtidigUttak,
            @JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom,
            @JsonProperty("trekkonto") Trekkonto trekkonto, @JsonProperty("fom") Integer trekkDager,
            @JsonProperty("arbeidstidprosent") Double arbeidstidProsent,
            @JsonProperty("utbetalingprosent") Double utbetalingprosent,
            @JsonProperty("gjelderAnnenPart") Boolean gjelderAnnenPart) {
        this.periodeResultatType = periodeResultatType;
        this.graderingInnvilget = graderingInnvilget;
        this.samtidigUttak = samtidigUttak;
        this.periode = new LukketPeriode(fom, tom);
        this.trekkonto = trekkonto;
        this.trekkDager = trekkDager;
        this.arbeidstidProsent = arbeidstidProsent;
        this.utbetalingprosent = utbetalingprosent;
        this.gjelderAnnenPart = gjelderAnnenPart;
    }
}
