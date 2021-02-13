package no.nav.foreldrepenger.mottak.innsending.varsel;

import java.time.LocalDateTime;

import no.nav.foreldrepenger.mottak.domain.felles.Person;

public record Varsel(LocalDateTime dato, Person søker) {

}
