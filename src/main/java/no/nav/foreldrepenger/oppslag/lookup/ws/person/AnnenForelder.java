package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import java.time.LocalDate;
import java.util.Objects;

public class AnnenForelder {

    private final Navn navn;
    private final Fodselsnummer fnr;
    private final LocalDate fødselsdato;

    public AnnenForelder(Navn navn, Fodselsnummer fnr, LocalDate fødselsdato) {
        this.navn = navn;
        this.fnr = fnr;
        this.fødselsdato = fødselsdato;
    }

    public Navn getNavn() {
        return navn;
    }

    public Fodselsnummer getFnr() {
        return fnr;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnenForelder that = (AnnenForelder) o;
        return Objects.equals(fnr, that.fnr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fnr);
    }
}
