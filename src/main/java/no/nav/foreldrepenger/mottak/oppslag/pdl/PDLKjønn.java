package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = ANY)
record PDLKjønn(@JsonProperty("kjoenn") PDLKjønn.Kjønn kjønn) {

    static PDLKjønn mann() {
        return new PDLKjønn(Kjønn.MANN);
    }

    static PDLKjønn kvinne() {
        return new PDLKjønn(Kjønn.KVINNE);
    }

    static enum Kjønn {
        MANN,
        KVINNE,
        UKJENT
    }
}