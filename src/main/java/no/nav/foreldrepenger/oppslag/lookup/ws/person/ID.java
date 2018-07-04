package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import no.nav.foreldrepenger.oppslag.lookup.ws.aktor.AktorId;

import java.util.Objects;

public class ID {

    private final AktorId aktorId;
    private final Fødselsnummer fnr;

    public ID(AktorId aktorId, Fødselsnummer fnr) {
        this.aktorId = aktorId;
        this.fnr = fnr;
    }

    public AktorId getAktorId() {
        return aktorId;
    }

    public Fødselsnummer getFnr() {
        return fnr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ID id = (ID) o;
        return Objects.equals(aktorId, id.aktorId) &&
            Objects.equals(fnr, id.fnr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktorId, fnr);
    }

    @Override
    public String toString() {
        return "ID{" +
            "aktorId=" + aktorId +
            ", fnr=" + fnr +
            '}';
    }
}
