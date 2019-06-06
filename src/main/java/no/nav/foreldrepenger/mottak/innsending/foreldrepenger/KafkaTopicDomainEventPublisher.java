package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Kvittering;

@Component
@ConditionalOnMissingBean(LoggingDomainEventPublisher.class)
public class KafkaTopicDomainEventPublisher implements InnsendingDomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicDomainEventPublisher.class);

    @Override
    public void publishEvent(Kvittering kvittering) {
        LOG.info("Publiserer hendelse fra {}", kvittering);
    }
}
