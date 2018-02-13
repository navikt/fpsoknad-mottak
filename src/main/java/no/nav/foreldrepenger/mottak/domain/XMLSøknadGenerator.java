package no.nav.foreldrepenger.mottak.domain;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;

public interface XMLSøknadGenerator {

    String toXML(Søknad søknad);

    SoeknadsskjemaEngangsstoenad toDokmotModel(Søknad søknad);

}
