package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.MDCUtil;

@Component
@ConditionalOnProperty(value = "mottak.sender.domainevent.enabled", havingValue = "true")
public class KafkaTopicDomainEventPublisher implements InnsendingDomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicDomainEventPublisher.class);
    private final String topic;
    private final KafkaOperations<String, String> KafkaOperations;

    @Inject
    private ObjectMapper mapper;

    public KafkaTopicDomainEventPublisher(@Value("${mottak.sender.domainevent.topic}") String topic,
            KafkaTemplate<String, String> KafkaOperations) {
        this.topic = topic;
        this.KafkaOperations = KafkaOperations;
    }

    @Override
    public void publishEvent(Kvittering kvittering, SøknadEgenskap egenskap, List<String> vedlegg) {
        try {
            InnsendingEvent event = new InnsendingEvent(kvittering, egenskap, vedlegg);
            String payload = mapper.writeValueAsString(event);
            LOG.info("Publiserer hendelse {} ({}) på topic {}", event, payload, topic);

            Message<String> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(NAV_CALL_ID, MDCUtil.callId())
                    .build();
            KafkaOperations.send(message);
        } catch (Exception e) {
            LOG.warn("Kunne ikke publisere hendelse", e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [topic=" + topic + ", KafkaOperations=" + KafkaOperations + "]";
    }
}
