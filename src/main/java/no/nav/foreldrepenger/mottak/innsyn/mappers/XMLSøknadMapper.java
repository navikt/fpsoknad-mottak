package no.nav.foreldrepenger.mottak.innsyn.mappers;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.innsending.mappers.Mappable;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;

public interface XMLSøknadMapper extends Mappable {
    Søknad tilSøknad(String xml, SøknadEgenskap egenskap);
}
