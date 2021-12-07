package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;


public record AnnenPart(PersonDetaljer personDetaljer) {

    @JsonCreator
    public AnnenPart {
        Objects.requireNonNull(personDetaljer, "Persondetaljer kan ikke være null");
    }
}
