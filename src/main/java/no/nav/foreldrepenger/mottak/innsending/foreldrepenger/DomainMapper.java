package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsyn.VersjonsBevisst;

public interface DomainMapper extends VersjonsBevisst {

    static final String UKJENT_KODEVERKSVERDI = "-";

    String tilXML(Søknad søknad, AktorId søker);

    String tilXML(Endringssøknad endringssøknad, AktorId søker);

}
