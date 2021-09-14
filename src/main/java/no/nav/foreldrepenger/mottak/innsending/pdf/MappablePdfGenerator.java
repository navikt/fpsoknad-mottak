package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.mottak.innsending.mappers.Mappable;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public interface MappablePdfGenerator extends Mappable {

    byte[] generer(Søknad søknad, Person søker, SøknadEgenskap egenskap);

}
