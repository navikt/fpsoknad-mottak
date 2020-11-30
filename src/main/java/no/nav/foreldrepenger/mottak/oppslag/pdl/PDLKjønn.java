package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonProperty;

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