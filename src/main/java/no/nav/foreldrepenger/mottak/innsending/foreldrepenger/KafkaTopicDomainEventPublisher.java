package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

//@Component
@ConditionalOnProperty(value = "mottak.sender.domainevent.enabled", havingValue = "true")
public class KafkaTopicDomainEventPublisher implements InnsendingDomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicDomainEventPublisher.class);
    private final String topic;
    private final KafkaOperations<String, Kvittering> KafkaOperations;

    public KafkaTopicDomainEventPublisher(@Value("${mottak.sender.domainevent.topic}") String topic,
            KafkaTemplate<String, Kvittering> KafkaOperations) {
        this.topic = topic;
        this.KafkaOperations = KafkaOperations;
    }

    @Override
    public void publishEvent(Kvittering kvittering, SøknadEgenskap egenskap, List<String> vedlegg) {
        LOG.info("Publiserer hendelse fra {} for søknad {} med vedlegg {}", kvittering, egenskap, vedlegg);
        KafkaOperations.send(topic, kvittering);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [topic=" + topic + ", KafkaOperations=" + KafkaOperations + "]";
    }
}
