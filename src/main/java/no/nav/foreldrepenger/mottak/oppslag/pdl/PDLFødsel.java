package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = ANY)
record PDLFødsel(@JsonProperty("foedselsdato") LocalDate fødselsdato) {
}