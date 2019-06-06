package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.mottak.domain.Kvittering;

public interface InnsendingDomainEventPublisher {

    void publishEvent(Kvittering kvittering);

}
