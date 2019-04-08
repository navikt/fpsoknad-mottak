package no.nav.foreldrepenger.mottak.innsending.mappers;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public interface DomainMapper extends Mappable {

    static final String KOSOVO = "XXK";

    String tilXML(Søknad søknad, AktørId søker, SøknadEgenskap egenskap);

    String tilXML(Endringssøknad endringssøknad, AktørId søker, SøknadEgenskap egenskap);

}
