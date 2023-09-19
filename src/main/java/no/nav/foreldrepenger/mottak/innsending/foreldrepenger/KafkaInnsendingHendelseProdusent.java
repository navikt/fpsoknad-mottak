package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.common.util.MDCUtil.callId;
import static org.springframework.kafka.support.KafkaHeaders.KEY;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

import java.time.LocalDate;
import java.util.Optional;

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

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.mottak.util.JacksonWrapper;

@Component
@ConditionalOnProperty(value = "mottak.sender.domainevent.enabled", havingValue = "true")
public class KafkaInnsendingHendelseProdusent implements InnsendingHendelseProdusent {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaInnsendingHendelseProdusent.class);
    private static final Logger SECURE_LOG = LoggerFactory.getLogger("secureLogger");
    private final String topic;
    private final KafkaOperations<String, String> kafkaOperations;
    private final JacksonWrapper mapper;

    public KafkaInnsendingHendelseProdusent(@Value("${mottak.sender.domainevent.topic}") String topic,
            KafkaTemplate<String, String> kafkaOperations, JacksonWrapper mapper) {
        this.topic = topic;
        this.kafkaOperations = kafkaOperations;
        this.mapper = mapper;
    }

    @Override
    public void publiser(FordelResultat kvittering, String dialogId, Konvolutt konvolutt, InnsendingPersonInfo person) {
        var callId = callId();
        var førsteBehandlingsdato = førsteInntektsmeldingDag(konvolutt).orElse(null);
        var h = new InnsendingHendelse(person.aktørId(), person.fnr(), kvittering.journalId(), callId, dialogId,
            kvittering.saksnummer(), konvolutt, førsteBehandlingsdato);
        LOG.info("Publiserer hendelse {} på topic {}", h, topic);
        send(MessageBuilder
                .withPayload(mapper.writeValueAsString(h))
                .setHeader(TOPIC, topic)
                .setHeader(KEY, h.aktørId().value())
                .setHeader(NAV_CALL_ID, callId)
                .build());
    }

    private Optional<LocalDate> førsteInntektsmeldingDag(Konvolutt konvolutt) {
        if (konvolutt.erInitiellForeldrepenger()) {
            var søknad = (Søknad) konvolutt.getInnsending();
            return Optional.ofNullable(søknad.getFørsteInntektsmeldingDag());
        }
        return Optional.empty();
    }

    private void send(Message<String> melding) {
        try {
            var callId = callId();
            kafkaOperations.send(melding).thenAccept(result -> loggVellykket(result, callId)).exceptionally(ex -> loggFeilet(melding, ex, callId));
        } catch (Exception ex) {
            // send() er blocking blant annet inntil den har oppdaterte metadata fra clusteret
            loggFeilet(melding, ex, callId());
        }
    }

    private static Void loggFeilet(Message<String> melding, Throwable ex, String callId) {
        LOG.warn("Kunne ikke sende hendelse med callId {}, se secure logs for detaljer.", callId, ex);
        SECURE_LOG.warn("Kunne ikke sende hendelse med callId {}. Melding: {}", callId, melding.getPayload(), ex);
        return null;
    }

    private static void loggVellykket(SendResult<String, String> result, String callId) {
        LOG.info("Sendte hendelse med offset {} til partisjon {} på topic {} for calLId {} OK", result.getRecordMetadata().offset(),
            result.getRecordMetadata().partition(), result.getRecordMetadata().topic(), callId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[topic=" + topic + ", kafkaOperations=" + kafkaOperations + ", mapper="
                + mapper + "]";
    }
}
