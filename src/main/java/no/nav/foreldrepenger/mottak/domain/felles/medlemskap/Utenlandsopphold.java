package no.nav.foreldrepenger.mottak.domain.felles.medlemskap;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;

public record Utenlandsopphold(@NotNull CountryCode land, LukketPeriode varighet) {

    public LocalDate fom() {
        return varighet.fom();
    }

    public LocalDate tom() {
        return varighet.fom();
    }
}
