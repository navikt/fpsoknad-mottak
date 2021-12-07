package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2.persondetaljer;

import com.fasterxml.jackson.annotation.JsonCreator;
import no.nav.foreldrepenger.mottak.innsyn.fpinfoV2.PersonDetaljer;

import java.util.Objects;

public record AktørId(String value) implements PersonDetaljer {

    @JsonCreator
    public AktørId {
        Objects.requireNonNull(value, "AktørId kan ikke være null");
    }

    public String value() {
        return value;
    }

}
