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
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.JacksonWrapper;

@Component
@ConditionalOnProperty(value = "mottak.sender.domainevent.enabled", havingValue = "true")
public class KafkaInnsendingHendelseProdusent implements InnsendingHendelseProdusent {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaInnsendingHendelseProdusent.class);
    private final String topic;
    private final KafkaOperations<String, String> kafkaOperations;
    private final Oppslag oppslag;
    private final JacksonWrapper mapper;

    public KafkaInnsendingHendelseProdusent(@Value("${mottak.sender.domainevent.topic}") String topic,
            KafkaTemplate<String, String> kafkaOperations, JacksonWrapper mapper, Oppslag oppslag) {
        this.topic = topic;
        this.kafkaOperations = kafkaOperations;
        this.mapper = mapper;
        this.oppslag = oppslag;
    }

    @Override
    public void publiser(Kvittering kvittering, String referanseId, SøknadType type, List<String> vedlegg) {
        var hendelse = new InnsendingHendelse(oppslag.getAktørIdAsString(), referanseId,
                kvittering,
                type, vedlegg);
        LOG.info("Publiserer hendelse {} på topic {}", hendelse, topic);
        var message = MessageBuilder
                .withPayload(mapper.writeValueAsString(hendelse))
                .setHeader(TOPIC, topic)
                .setHeader(NAV_CALL_ID, callId())
                .build();
        send(message);
    }

    private void send(Message<String> melding) {
        kafkaOperations.send(melding).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOG.info("Sendte hendelse {} med offset {}", melding, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOG.warn("Kunne ikke sende melding {}", melding, ex);
            }
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[topic=" + topic + ", kafkaOperations=" + kafkaOperations + ", mapper="
                + mapper + "]";
    }
}
