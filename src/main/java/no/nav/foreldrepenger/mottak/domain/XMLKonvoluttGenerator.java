package no.nav.foreldrepenger.mottak.domain;

public interface XMLKonvoluttGenerator {

    String toXML(Søknad søknad);

    XMLSøknadGenerator getSøknadGenerator();

}
