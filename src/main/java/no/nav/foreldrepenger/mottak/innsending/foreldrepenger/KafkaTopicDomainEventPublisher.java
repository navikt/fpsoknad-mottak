package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Kvittering;

@Component
@ConditionalOnMissingBean(LoggingDomainEventPublisher.class)
public class KafkaTopicDomainEventPublisher implements InnsendingDomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicDomainEventPublisher.class);
    private final String topic;
    private final KafkaTemplate<String, Kvittering> kafkaTemplate;

    public KafkaTopicDomainEventPublisher(@Value("${mottak.søknadsender.domainevent.topic}") String topic,
            KafkaTemplate<String, Kvittering> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishEvent(Kvittering kvittering) {
        LOG.info("Publiserer hendelse fra {}", kvittering);
        kafkaTemplate.send(topic, kvittering);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [topic=" + topic + ", kafkaTemplate=" + kafkaTemplate + "]";
    }
}
