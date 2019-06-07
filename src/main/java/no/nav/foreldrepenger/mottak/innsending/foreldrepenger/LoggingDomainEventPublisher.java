package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Component
@ConditionalOnProperty(value = "mottak.sender.domainevent.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingDomainEventPublisher implements InnsendingDomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingDomainEventPublisher.class);

    @Override
    public void publishEvent(Kvittering kvittering, SøknadEgenskap egenskap) {
        LOG.info("Publiserer hendelse fra {} for søknad {}", kvittering, egenskap);
    }
}
