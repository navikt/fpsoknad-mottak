package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public class AnnenPart {
    private PersonDetaljer personDetaljer;

    @JsonCreator
    public AnnenPart(PersonDetaljer personDetaljer) {
        Objects.requireNonNull(personDetaljer, "ident må være non-null");
        this.personDetaljer = personDetaljer;
    }

    public PersonDetaljer getPersonDetaljer() {
        return personDetaljer;
    }

    public void setPersonDetaljer(PersonDetaljer personDetaljer) {
        Objects.requireNonNull(personDetaljer, "ident må være non-null");
        this.personDetaljer = personDetaljer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnenPart annenPart = (AnnenPart) o;
        return Objects.equals(personDetaljer, annenPart.personDetaljer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personDetaljer);
    }
}
