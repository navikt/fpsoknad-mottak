package no.nav.foreldrepenger.mottak.util;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;

public interface SøknadInspektør {

    Versjon versjon(String xml);

    SøknadType type(no.nav.vedtak.felles.xml.soeknad.v1.Soeknad søknad);

    SøknadType type(no.nav.vedtak.felles.xml.soeknad.v2.Soeknad søknad);

    boolean erEngangsstønad(String xml);

}
