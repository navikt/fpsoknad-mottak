package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.mappers.Mappable;

public interface MappablePdfGenerator extends Mappable {

    byte[] generer(Søknad søknad, Person søker, SøknadEgenskap egenskap);

}
