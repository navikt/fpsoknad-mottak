package no.nav.foreldrepenger.mottak.domain;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;

public interface XMLGenerator {

    String toXML(Søknad søknad);

    SoeknadsskjemaEngangsstoenad toModel(Søknad søknad);

}
