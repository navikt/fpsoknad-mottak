package no.nav.foreldrepenger.mottak.innsyn.mappers;

import no.nav.foreldrepenger.mottak.Mappable;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public interface XMLMapper extends Mappable {

    String UKJENT_KODEVERKSVERDI = "-";

    Søknad tilSøknad(String xml, SøknadEgenskap egenskap);
}
