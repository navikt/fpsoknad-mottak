package no.nav.foreldrepenger.mottak.util;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;

public interface SøknadInspektør {

    SøknadInspeksjonResultat inspiser(String xml);

    Versjon versjon(String xml);

    SøknadType type(String xml);

}
