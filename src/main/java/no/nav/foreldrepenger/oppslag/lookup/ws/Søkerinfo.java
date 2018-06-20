package no.nav.foreldrepenger.oppslag.lookup.ws;

import no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Person;

import java.util.List;

public class Søkerinfo {

    public Person person;
    public List<Arbeidsforhold> arbeidsforhold;

    public Søkerinfo(Person person, List<Arbeidsforhold> arbeidsforhold) {
        this.person = person;
        this.arbeidsforhold = arbeidsforhold;
    }
}
