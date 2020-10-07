package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PDLNavn(String fornavn, String mellomnavn, String etternavn) {
}
