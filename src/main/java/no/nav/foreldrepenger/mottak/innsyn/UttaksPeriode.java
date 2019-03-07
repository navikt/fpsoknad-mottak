package no.nav.foreldrepenger.mottak.innsyn;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.StønadskontoType;

@Data
public class UttaksPeriode {

    private final PeriodeResultatType periodeResultatType;
    private final Boolean graderingInnvilget;
    private final Boolean samtidigUttak;
    private final LukketPeriode periode;
    private final StønadskontoType stønadskontotype;
    private final Integer trekkDager;
    private final Double arbeidstidProsent;
    private final Double utbetalingprosent;
    private final Boolean gjelderAnnenPart;

    public UttaksPeriode(@JsonProperty("periodeResultatType") PeriodeResultatType periodeResultatType,
            @JsonProperty("graderingInnvilget") Boolean graderingInnvilget,
            @JsonProperty("samtidigUttak") Boolean samtidigUttak,
            @JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom,
            @JsonProperty("stønadskontotype") @JsonAlias("trekkonto") StønadskontoType stønadskontotype,
            @JsonProperty("trekkDager") Integer trekkDager,
            @JsonProperty("arbeidstidprosent") Double arbeidstidProsent,
            @JsonProperty("utbetalingprosent") Double utbetalingprosent,
            @JsonProperty("gjelderAnnenPart") Boolean gjelderAnnenPart) {
        this.periodeResultatType = periodeResultatType;
        this.graderingInnvilget = graderingInnvilget;
        this.samtidigUttak = samtidigUttak;
        this.periode = new LukketPeriode(fom, tom);
        this.stønadskontotype = stønadskontotype;
        this.trekkDager = trekkDager;
        this.arbeidstidProsent = arbeidstidProsent;
        this.utbetalingprosent = utbetalingprosent;
        this.gjelderAnnenPart = gjelderAnnenPart;
    }
}
