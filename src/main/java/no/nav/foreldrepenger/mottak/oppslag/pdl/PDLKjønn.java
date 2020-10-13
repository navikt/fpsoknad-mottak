package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data class PDLKjønn {
    private final PDLKjønn.Kjønn kjønn;

    @JsonCreator
    public PDLKjønn(@JsonProperty("kjoenn") PDLKjønn.Kjønn kjønn) {
        this.kjønn = kjønn;
    }

    static enum Kjønn {
        MANN,
        KVINNE,
        UKJENT
    }
}