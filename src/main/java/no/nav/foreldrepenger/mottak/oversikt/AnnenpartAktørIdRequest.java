package no.nav.foreldrepenger.mottak.oversikt;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;

public record AnnenpartAktørIdRequest(@Valid @NotNull Fødselsnummer annenPartFødselsnummer) {
}
