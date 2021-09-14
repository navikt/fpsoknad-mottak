package no.nav.foreldrepenger.mottak.innsyn.uttaksplan;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Oppholdsårsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.mottak.innsyn.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.mottak.innsyn.PeriodeResultatType;

@Data
public class UttaksPeriode implements Comparable<UttaksPeriode> {

    private final Oppholdsårsak oppholdAarsak;
    private final Overføringsårsak overfoeringAarsak;
    private final UtsettelsePeriodeType utsettelsePeriodeType;
    private final PeriodeResultatType periodeResultatType;
    private final Boolean graderingInnvilget;
    private final Boolean samtidigUttak;
    private final MorsAktivitet morsAktivitet;
    private final LukketPeriode periode;
    private final StønadskontoType stønadskontotype;
    private final Double trekkDager;
    private final Integer arbeidstidProsent;
    private final Integer utbetalingsprosent;
    private final Boolean gjelderAnnenPart;
    private final GraderingAvslagÅrsak graderingAvslagAarsak;
    private final Boolean flerbarnsdager;
    private final Boolean manueltBehandlet;
    private final Integer samtidigUttaksprosent;
    private final UttakArbeidType uttakArbeidType;
    private final ArbeidsgiverInfo arbeidsgiverInfo;
    private final String periodeResultatÅrsak;

    public UttaksPeriode(@JsonProperty("oppholdAarsak") Oppholdsårsak oppholdAarsak,
                         @JsonProperty("overfoeringAarsak") Overføringsårsak overfoeringAarsak,
                         @JsonProperty("graderingAvslagAarsak") GraderingAvslagÅrsak graderingAvslagAarsak,
                         @JsonProperty("utsettelsePeriodeType") UtsettelsePeriodeType utsettelsePeriodeType,
                         @JsonProperty("periodeResultatType") PeriodeResultatType periodeResultatType,
                         @JsonProperty("graderingInnvilget") Boolean graderingInnvilget,
                         @JsonProperty("samtidigUttak") Boolean samtidigUttak,
                         @JsonProperty("fom") LocalDate fom,
                         @JsonProperty("tom") LocalDate tom,
                         @JsonProperty("stønadskontotype") @JsonAlias("trekkonto") StønadskontoType stønadskontotype,
                         @JsonProperty("trekkDager") Double trekkDager,
                         @JsonProperty("arbeidstidprosent") Integer arbeidstidProsent,
                         @JsonProperty("utbetalingsprosent") Integer utbetalingsprosent,
                         @JsonProperty("gjelderAnnenPart") Boolean gjelderAnnenPart,
                         @JsonProperty("manueltBehandlet") Boolean manueltBehandlet,
                         @JsonProperty("samtidigUttaksprosent") Integer samtidigUttaksprosent,
                         @JsonProperty("morsAktivitet") MorsAktivitet morsAktivitet,
                         @JsonProperty("flerbarnsdager") Boolean flerbarnsdager,
                         @JsonProperty("uttakArbeidType") UttakArbeidType uttakArbeidType,
                         @JsonProperty("arbeidsgiverInfo") ArbeidsgiverInfo arbeidsgiverInfo,
                         @JsonProperty("periodeResultatÅrsak") String periodeResultatÅrsak) {
        this.oppholdAarsak = oppholdAarsak;
        this.overfoeringAarsak = overfoeringAarsak;
        this.utsettelsePeriodeType = utsettelsePeriodeType;
        this.periodeResultatType = periodeResultatType;
        this.graderingInnvilget = graderingInnvilget;
        this.samtidigUttak = samtidigUttak;
        this.periode = new LukketPeriode(fom, tom);
        this.stønadskontotype = stønadskontotype;
        this.trekkDager = trekkDager;
        this.arbeidstidProsent = arbeidstidProsent;
        this.utbetalingsprosent = utbetalingsprosent;
        this.gjelderAnnenPart = gjelderAnnenPart;
        this.graderingAvslagAarsak = graderingAvslagAarsak;
        this.manueltBehandlet = manueltBehandlet;
        this.samtidigUttaksprosent = samtidigUttaksprosent;
        this.morsAktivitet = morsAktivitet;
        this.flerbarnsdager = flerbarnsdager;
        this.uttakArbeidType = uttakArbeidType;
        this.arbeidsgiverInfo = arbeidsgiverInfo;
        this.periodeResultatÅrsak = periodeResultatÅrsak;
    }

    @Override
    public int compareTo(UttaksPeriode other) {
        return this.getPeriode().fom().compareTo(other.getPeriode().fom());
    }
}
