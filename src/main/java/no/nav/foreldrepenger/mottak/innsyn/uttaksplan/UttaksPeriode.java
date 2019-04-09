package no.nav.foreldrepenger.mottak.innsyn.uttaksplan;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Oppholdsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.mottak.innsyn.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.mottak.innsyn.PeriodeResultatType;

@Data
public class UttaksPeriode {

    private final Oppholdsårsak oppholdÅrsak;
    private final Overføringsårsak overføringÅrsak;
    private final UtsettelsePeriodeType utsettelsePeriodeType;
    private final PeriodeResultatType periodeResultatType;
    private final Boolean graderingInnvilget;
    private final Boolean samtidigUttak;
    private final LukketPeriode periode;
    private final StønadskontoType stønadskontotype;
    private final Integer trekkDager;
    private final Integer arbeidstidProsent;
    private final Integer utbetalingprosent;
    private final Boolean gjelderAnnenPart;
    private final GraderingAvslagÅrsak graderingAvslagÅrsak;
    private final Boolean flerbarnsdager;
    private final Boolean manueltBehandlet;
    private final Integer  samtidigUttaksprosent;

    public UttaksPeriode(@JsonProperty("oppholdAarsak") Oppholdsårsak oppholdÅrsak,
            @JsonProperty("overfoeringAarsak") Overføringsårsak overføringÅrsak,
            @JsonProperty("utsettelsePeriodeType") UtsettelsePeriodeType utsettelsePeriodeType,
            @JsonProperty("periodeResultatType") PeriodeResultatType periodeResultatType,
            @JsonProperty("graderingInnvilget") Boolean graderingInnvilget,
            @JsonProperty("samtidigUttak") Boolean samtidigUttak,
            @JsonProperty("fom") LocalDate fom,
            @JsonProperty("tom") LocalDate tom,
            @JsonProperty("stønadskontotype") @JsonAlias("trekkonto") StønadskontoType stønadskontotype,
            @JsonProperty("trekkDager") Integer trekkDager,
            @JsonProperty("arbeidstidprosent") Integer arbeidstidProsent,
            @JsonProperty("utbetalingprosent") Integer utbetalingprosent,
            @JsonProperty("gjelderAnnenPart") Boolean gjelderAnnenPart,
            @JsonProperty("graderingAvslagAarsak") GraderingAvslagÅrsak graderingAvslagÅrsak,
            @JsonProperty("manueltBehandlet") Boolean manueltBehandlet,
            @JsonProperty("samtidigUttaksprosent") Integer samtidigUttaksprosent,
            @JsonProperty("flerbarnsdager") Boolean flerbarnsdager) {
        this.oppholdÅrsak = oppholdÅrsak;
        this.overføringÅrsak = overføringÅrsak;
        this.utsettelsePeriodeType = utsettelsePeriodeType;
        this.periodeResultatType = periodeResultatType;
        this.graderingInnvilget = graderingInnvilget;
        this.samtidigUttak = samtidigUttak;
        this.periode = new LukketPeriode(fom, tom);
        this.stønadskontotype = stønadskontotype;
        this.trekkDager = trekkDager;
        this.arbeidstidProsent = arbeidstidProsent;
        this.utbetalingprosent = utbetalingprosent;
        this.gjelderAnnenPart = gjelderAnnenPart;
        this.graderingAvslagÅrsak = graderingAvslagÅrsak;
        this.manueltBehandlet = manueltBehandlet;
        this.samtidigUttaksprosent = samtidigUttaksprosent;
        this.flerbarnsdager  = flerbarnsdager;
    }
}
