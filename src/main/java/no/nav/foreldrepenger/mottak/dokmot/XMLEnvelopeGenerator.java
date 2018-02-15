package no.nav.foreldrepenger.mottak.dokmot;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.XMLSøknadGenerator;

public interface XMLEnvelopeGenerator {

    String toXML(Søknad søknad);

    XMLSøknadGenerator getSøknadGenerator();

}
