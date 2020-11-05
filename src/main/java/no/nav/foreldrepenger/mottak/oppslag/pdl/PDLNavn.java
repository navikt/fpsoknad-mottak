package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = ANY)
record PDLWrappedNavn(@JsonProperty("navn") Set<PDLNavn> navn) {

}

@JsonAutoDetect(fieldVisibility = ANY)
record PDLNavn(String fornavn, String mellomnavn, String etternavn) {
}
