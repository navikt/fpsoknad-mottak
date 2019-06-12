package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public class LoggingDomainEventPublisher implements InnsendingDomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingDomainEventPublisher.class);

    @Override
    public void publishEvent(Kvittering kvittering, SøknadEgenskap egenskap, List<String> vedlegg) {
        LOG.info("Publiserer hendelse fra {} for søknad {} med vedlegg {}", kvittering, egenskap, vedlegg);
    }
}
