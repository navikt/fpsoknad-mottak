package no.nav.foreldrepenger.mottak.innsending.varsel;

import java.time.LocalDateTime;

import no.nav.foreldrepenger.mottak.domain.felles.Person;

public class Varsel {
    private final LocalDateTime dato;
    private final Person søker;

    public Varsel(LocalDateTime dato, Person søker) {
        this.dato = dato;
        this.søker = søker;
    }

    public LocalDateTime getDato() {
        return dato;
    }

    public Person getSøker() {
        return søker;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dato=" + dato + ", søker=" + søker + "]";
    }
}
