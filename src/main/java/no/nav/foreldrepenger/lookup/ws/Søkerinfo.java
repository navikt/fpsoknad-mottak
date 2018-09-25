package no.nav.foreldrepenger.lookup.ws;

import java.util.List;

import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.lookup.ws.person.Person;

public class Søkerinfo {

    private final Person person;
    private final List<Arbeidsforhold> arbeidsforhold;

    public Søkerinfo(Person person, List<Arbeidsforhold> arbeidsforhold) {
        this.person = person;
        this.arbeidsforhold = arbeidsforhold;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [person=" + person + ", arbeidsforhold=" + arbeidsforhold + "]";
    }
}
