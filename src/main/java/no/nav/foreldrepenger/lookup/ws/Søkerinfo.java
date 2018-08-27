package no.nav.foreldrepenger.lookup.ws;

import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.lookup.ws.person.Person;

import java.util.List;

public class Søkerinfo {

    public Person person;
    public List<Arbeidsforhold> arbeidsforhold;

    public Søkerinfo(Person person, List<Arbeidsforhold> arbeidsforhold) {
        this.person = person;
        this.arbeidsforhold = arbeidsforhold;
    }
}
