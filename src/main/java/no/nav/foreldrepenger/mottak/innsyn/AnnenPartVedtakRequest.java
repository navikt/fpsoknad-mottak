package no.nav.foreldrepenger.mottak.innsyn;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.AktørId;

public record AnnenPartVedtakRequest(AktørId aktørId,
                                     AktørId annenPartAktørId,
                                     AktørId barnAktørId,
                                     LocalDate familiehendelse) {
}
