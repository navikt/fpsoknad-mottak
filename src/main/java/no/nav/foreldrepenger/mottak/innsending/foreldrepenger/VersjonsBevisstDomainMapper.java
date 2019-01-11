package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.util.Versjon;

public interface VersjonsBevisstDomainMapper extends DomainMapper {

    String tilXML(Søknad søknad, AktorId søker, Versjon versjon);

    String tilXML(Endringssøknad endringssøknad, AktorId søker, Versjon versjon);

    default String tilXML(Søknad søknad, AktorId søker) {
        return tilXML(søknad, søker, DEFAULT_VERSJON);
    }

    default String tilXML(Endringssøknad endringssøknad, AktorId søker) {
        return tilXML(endringssøknad, søker, DEFAULT_VERSJON);
    }
}
