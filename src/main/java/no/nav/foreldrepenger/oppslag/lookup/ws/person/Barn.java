package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import no.nav.foreldrepenger.oppslag.lookup.Pair;

import java.time.LocalDate;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Barn {

    private final Pair<Fodselsnummer, Fodselsnummer> fnrs;
    private final LocalDate fødselsdato;
    private final Navn navn;
    private final Kjønn kjønn;

    public Barn(Fodselsnummer fnrMor, Fodselsnummer fnr, LocalDate fødselsdato, Navn navn, Kjønn kjønn) {
        this.fnrs = Pair.of(fnr, fnrMor);
        this.fødselsdato = requireNonNull(fødselsdato);
        this.navn = navn;
        this.kjønn = kjønn;
    }

    @JsonUnwrapped
    public Fodselsnummer getFnr() {
        return fnrs.getFirst();
    }

    @JsonUnwrapped
    public Fodselsnummer getFnrMor() {
        return fnrs.getSecond();
    }

    @JsonUnwrapped
    public Navn getNavn() {
        return navn;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    public Kjønn getKjønn() {
        return kjønn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fnrs, fødselsdato);
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
        return Objects.equals(fnrs, that.fnrs) && Objects.equals(fødselsdato, that.fødselsdato);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + fnrs.getFirst() + ", fnrMor=" + fnrs.getSecond() + ", fødselsdato="
            + fødselsdato + "]";
    }
}
