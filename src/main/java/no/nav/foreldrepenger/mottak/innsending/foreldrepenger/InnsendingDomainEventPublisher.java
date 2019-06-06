package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public interface InnsendingDomainEventPublisher {

    void publishEvent(Kvittering kvittering, SøknadEgenskap egenskap);

}
