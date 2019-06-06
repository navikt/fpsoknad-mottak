package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Component
@ConditionalOnMissingBean(LoggingDomainEventPublisher.class)
public class KafkaTopicDomainEventPublisher implements InnsendingDomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicDomainEventPublisher.class);
    private final String topic;
    private final KafkaOperations<String, Kvittering> KafkaOperations;

    public KafkaTopicDomainEventPublisher(@Value("${mottak.søknadsender.domainevent.topic}") String topic,
            KafkaTemplate<String, Kvittering> KafkaOperations) {
        this.topic = topic;
        this.KafkaOperations = KafkaOperations;
    }

    @Override
    public void publishEvent(Kvittering kvittering, SøknadEgenskap egenskap) {
        LOG.info("Publiserer hendelse fra {} for søknad {}", kvittering, egenskap);
        KafkaOperations.send(topic, kvittering);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [topic=" + topic + ", KafkaOperations=" + KafkaOperations + "]";
    }
}
