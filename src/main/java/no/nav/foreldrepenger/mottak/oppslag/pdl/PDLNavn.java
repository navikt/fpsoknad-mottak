package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = ANY)
record PDLNavn(String fornavn, String mellomnavn, String etternavn) {
}

@JsonAutoDetect(fieldVisibility = ANY)
record PDLWrappedNavn(PDLNavn navn) {

}