package no.nav.foreldrepenger.mottak.innsyn.mappers;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.mappers.Mappable;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public interface XMLSøknadMapper extends Mappable {

    final String UKJENT_KODEVERKSVERDI = "-";

    Søknad tilSøknad(String xml, SøknadEgenskap egenskap);
}
