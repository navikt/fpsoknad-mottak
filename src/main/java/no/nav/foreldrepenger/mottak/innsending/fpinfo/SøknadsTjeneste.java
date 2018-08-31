package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import no.nav.foreldrepenger.mottak.domain.Søknad;

public interface SøknadsTjeneste {

    Søknad hentSøknad(String behandlingsId);

}
