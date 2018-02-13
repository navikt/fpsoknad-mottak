package no.nav.foreldrepenger.mottak.dokmot;

import no.nav.foreldrepenger.mottak.domain.Søknad;

public interface XMLEnvelopeGenerator {

    String toXML(Søknad søknad);

}
