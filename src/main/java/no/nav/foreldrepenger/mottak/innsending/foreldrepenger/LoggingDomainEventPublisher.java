package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Kvittering;

@Component
@ConditionalOnProperty(value = "no.nav.foreldrepenger.mottak.søknadsender.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingDomainEventPublisher implements InnsendingDomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingDomainEventPublisher.class);

    @Override
    public void publishEvent(Kvittering kvittering) {
        LOG.info("Publiserer hendelse fra {}", kvittering);
    }
}
