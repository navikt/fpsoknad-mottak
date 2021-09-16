package no.nav.foreldrepenger.mottak.innsyn.mappers;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsending.mappers.Mappable;

public interface XMLSøknadMapper extends Mappable {
    Søknad tilSøknad(String xml, SøknadEgenskap egenskap);
}
