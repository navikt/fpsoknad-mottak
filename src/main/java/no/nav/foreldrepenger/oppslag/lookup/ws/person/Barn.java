package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import no.nav.foreldrepenger.oppslag.lookup.Pair;

import java.time.LocalDate;
import java.util.Objects;

public class Barn {

    private final Pair<Fodselsnummer, Fodselsnummer> fnrs;

    private final LocalDate birthDate;

    public Barn(Fodselsnummer fnrMor, Fodselsnummer fnr, LocalDate birthDate) {
        this.fnrs = Pair.of(fnr, fnrMor);
        this.birthDate = Objects.requireNonNull(birthDate);
    }

    public Fodselsnummer getFnr() {
        return fnrs.getFirst();
    }

    public Fodselsnummer getFnrMor() {
        return fnrs.getSecond();
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fnrs, birthDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        Barn that = (Barn) o;
        return Objects.equals(fnrs, that.fnrs) && Objects.equals(birthDate, that.birthDate);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + fnrs.getFirst() + ", fnrMor=" + fnrs.getSecond() + ", birthDate="
            + birthDate + "]";
    }
}
