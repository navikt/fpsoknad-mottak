package no.nav.foreldrepenger.oppslag.lookup.ws;

import no.nav.foreldrepenger.oppslag.lookup.ws.aareg.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Person;

import java.util.List;

public class Oppslag {

    public Person person;
    public List<Arbeidsforhold> arbeidsforhold;

    public Oppslag(Person person, List<Arbeidsforhold> arbeidsforhold) {
        this.person = person;
        this.arbeidsforhold = arbeidsforhold;
    }
}
