package no.nav.foreldrepenger.mottak.innsyn;

import java.time.LocalDate;

import jakarta.validation.Valid;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;

record AnnenPartVedtakIdentifikator(@Valid Fødselsnummer annenPartFødselsnummer,
                                    @Valid Fødselsnummer barnFødselsnummer,
                                    LocalDate familiehendelse) {
}
