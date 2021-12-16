package no.nav.foreldrepenger.mottak.innsyn.fpinfov2.persondetaljer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.foreldrepenger.mottak.innsyn.fpinfov2.PersonDetaljer;

import java.util.Objects;

public record AktørId(String value) implements PersonDetaljer {

    @JsonCreator
    public AktørId {
        Objects.requireNonNull(value, "AktørId kan ikke være null");
    }

    @JsonProperty("aktørId")
    public String value() {
        return value;
    }

}
