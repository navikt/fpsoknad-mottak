package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.util.Versjon;

public interface VersjonerbarDomainMapper extends DomainMapper {

    String tilXML(Søknad søknad, AktorId søker, Versjon versjon);

    String tilXML(Endringssøknad endringssøknad, AktorId søker, Versjon versjon);

}
