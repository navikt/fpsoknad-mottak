package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import no.nav.foreldrepenger.mottak.http.RetryAware;
import no.nav.foreldrepenger.mottak.innsending.Pingable;

public interface Organisasjon extends Pingable, RetryAware {

    String organisasjonsNavn(String orgnr);

}
