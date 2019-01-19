package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.util.Versjon;

public interface SøknadInspektør {

    SøknadEgenskaper inspiser(String xml);

    default SøknadType type(String xml) {
        return inspiser(xml).getType();
    }

    default Versjon versjon(String xml) {
        return inspiser(xml).getVersjon();
    }

}
