package no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;

public record HentKonto(Fødselsnummer kontohaver, boolean medHistorikk) {
}
