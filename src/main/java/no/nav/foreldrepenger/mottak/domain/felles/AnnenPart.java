package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record AnnenPart(Fødselsnummer fnr, AktørId aktørId, Navn navn, LocalDate fødselsdato) {

}
