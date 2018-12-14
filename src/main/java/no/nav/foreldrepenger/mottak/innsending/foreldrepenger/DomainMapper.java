package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.util.Versjonerbar;

public interface DomainMapper extends Versjonerbar {

    static final String UKJENT_KODEVERKSVERDI = "-";

    String tilXML(Søknad søknad, AktorId søker);

    String tilXML(Endringssøknad endringssøknad, AktorId søker);

}
