package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.mottak.Mappable;
import no.nav.foreldrepenger.mottak.domain.Søknad;

public interface XMLMapper extends Mappable {

    String UKJENT_KODEVERKSVERDI = "-";

    Søknad tilSøknad(String xml, SøknadEgenskap egenskap);
}
