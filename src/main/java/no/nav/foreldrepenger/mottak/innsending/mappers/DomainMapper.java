package no.nav.foreldrepenger.mottak.innsending.mappers;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public interface DomainMapper extends Mappable {

    String KOSOVO = "XXK";

    String tilXML(Søknad søknad, AktørId søker, SøknadEgenskap egenskap);

    String tilXML(Endringssøknad endringssøknad, AktørId søker, SøknadEgenskap egenskap);

}
