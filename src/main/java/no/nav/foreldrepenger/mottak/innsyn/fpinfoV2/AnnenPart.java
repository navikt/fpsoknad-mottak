package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public record AnnenPart(AktørId aktørId) {

    @JsonCreator
    public AnnenPart {
        Objects.requireNonNull(aktørId, "aktørId må være non-null");
    }
}
