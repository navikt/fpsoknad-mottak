package no.nav.foreldrepenger.mottak.domain;

import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;

public interface XMLKonvoluttGenerator {

    String toXML(Søknad søknad);

    XMLSøknadGenerator getSøknadGenerator();

    Dokumentforsendelse toDokmotModel(Søknad søknad);

}
