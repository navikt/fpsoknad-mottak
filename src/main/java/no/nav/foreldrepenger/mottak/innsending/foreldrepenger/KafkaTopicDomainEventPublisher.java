package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.JacksonUtil;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Component
@ConditionalOnProperty(value = "mottak.sender.domainevent.enabled", havingValue = "true")
public class KafkaTopicDomainEventPublisher implements InnsendingDomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicDomainEventPublisher.class);
    private final String topic;
    private final KafkaOperations<String, String> kafkaOperations;
    private final Oppslag oppslag;
    private final JacksonUtil mapper;
    private final TokenUtil tokenUtil;

    public KafkaTopicDomainEventPublisher(@Value("${mottak.sender.domainevent.topic}") String topic,
            KafkaTemplate<String, String> kafkaOperations, JacksonUtil mapper, Oppslag oppslag, TokenUtil tokenUtil) {
        this.topic = topic;
        this.kafkaOperations = kafkaOperations;
        this.mapper = mapper;
        this.oppslag = oppslag;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public void publishEvent(Kvittering kvittering, SøknadEgenskap egenskap, List<String> vedlegg) {
        InnsendingEvent event = new InnsendingEvent(oppslag.getAktørIdAsString(), tokenUtil.getSubject(), kvittering,
                egenskap, vedlegg);
        LOG.info("Publiserer hendelse {} på topic {}", event, topic);
        Message<String> message = MessageBuilder
                .withPayload(mapper.writeValueAsString(event))
                .setHeader(TOPIC, topic)
                .setHeader(NAV_CALL_ID, callId())
                .build();
        send(message);
    }

    private void send(Message<String> message) {
        kafkaOperations.send(message).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOG.info("Sendte hendelse {} med offset {}", message,
                        result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOG.warn("Kunne ikke sende melding {}", message, ex);
            }
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[topic=" + topic + ", kafkaOperations=" + kafkaOperations + ", mapper="
                + mapper + "]";
    }
}
