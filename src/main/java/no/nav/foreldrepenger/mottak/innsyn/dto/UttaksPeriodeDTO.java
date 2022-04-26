package no.nav.foreldrepenger.mottak.innsyn.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Oppholdsårsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.common.innsyn.uttaksplan.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.common.innsyn.uttaksplan.PeriodeResultatType;
import no.nav.foreldrepenger.common.innsyn.uttaksplan.UtsettelsePeriodeType;
import no.nav.foreldrepenger.common.innsyn.uttaksplan.UttakArbeidType;

public record UttaksPeriodeDTO(Oppholdsårsak oppholdAarsak,
                               Overføringsårsak overfoeringAarsak,
                               UtsettelsePeriodeType utsettelsePeriodeType,
                               PeriodeResultatType periodeResultatType,
                               Boolean graderingInnvilget,
                               Boolean samtidigUttak,
                               LukketPeriode periode,
                               LocalDate fom,
                               LocalDate tom,
                               @JsonAlias("trekkonto") StønadskontoType stønadskontotype,
                               @JsonProperty("trekkdager") BigDecimal trekkDager,
                               @JsonProperty("arbeidstidprosent") Integer arbeidstidProsent,
                               Integer utbetalingsprosent,
                               Boolean gjelderAnnenPart,
                               GraderingAvslagÅrsak graderingAvslagAarsak,
                               String periodeResultatÅrsak,
                               MorsAktivitet morsAktivitet,
                               Boolean flerbarnsdager,
                               Boolean manueltBehandlet,
                               Integer samtidigUttaksprosent,
                               UttakArbeidType uttakArbeidType,
                               AktørId arbeidsgiverAktoerId,
                               Orgnummer arbeidsgiverOrgnr) implements Comparable<UttaksPeriodeDTO> {

    public UttaksPeriodeDTO {
        periode = new LukketPeriode(fom, tom);
    }

    @Override
    public int compareTo(UttaksPeriodeDTO other) {
        return this.periode().fom().compareTo(other.periode().fom());
    }
}
