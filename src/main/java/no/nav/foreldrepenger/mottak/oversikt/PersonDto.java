package no.nav.foreldrepenger.mottak.oversikt;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;

public record PersonDto(AktørId aktørid,
                        Fødselsnummer fnr,
                        Navn navn) {
}
