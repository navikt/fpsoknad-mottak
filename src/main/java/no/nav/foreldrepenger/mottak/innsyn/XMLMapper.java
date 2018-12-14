package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.util.Versjonerbar;

public interface XMLMapper extends Versjonerbar {

    String VERSJONERBAR = "multiple";

    Søknad tilSøknad(String xml);

}
