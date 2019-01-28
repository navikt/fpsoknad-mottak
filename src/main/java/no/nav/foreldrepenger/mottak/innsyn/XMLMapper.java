package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.mottak.EgenskapsBevisst;
import no.nav.foreldrepenger.mottak.domain.Søknad;

public interface XMLMapper extends EgenskapsBevisst {

    String DELEGERENDE = "delegerende";
    String UKJENT_KODEVERKSVERDI = "-";

    Søknad tilSøknad(String xml, SøknadEgenskap egenskap);
}
