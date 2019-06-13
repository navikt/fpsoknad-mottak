package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.JacksonUtil;

@Component
@ConditionalOnProperty(value = "mottak.sender.domainevent.enabled", havingValue = "true")
public class KafkaTopicDomainEventPublisher implements InnsendingDomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicDomainEventPublisher.class);
    private final String topic;
    private final KafkaOperations<String, String> KafkaOperations;

    @Inject
    private JacksonUtil mapper;

    public KafkaTopicDomainEventPublisher(@Value("${mottak.sender.domainevent.topic}") String topic,
            KafkaTemplate<String, String> KafkaOperations) {
        this.topic = topic;
        this.KafkaOperations = KafkaOperations;
    }

    @Override
    public void publishEvent(Kvittering kvittering, SøknadEgenskap egenskap, List<String> vedlegg) {
        InnsendingEvent event = new InnsendingEvent(kvittering, egenskap, vedlegg);
        LOG.info("Publiserer hendelse {} på topic {}", event, topic);
        Message<String> message = MessageBuilder
                .withPayload(mapper.writeValueAsString(event))
                .setHeader(TOPIC, topic)
                .setHeader(NAV_CALL_ID, callId())
                .build();
        KafkaOperations.send(message);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [topic=" + topic + ", KafkaOperations=" + KafkaOperations + "]";
    }
}
