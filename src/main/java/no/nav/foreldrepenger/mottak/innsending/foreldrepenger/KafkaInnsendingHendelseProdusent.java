package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.JacksonWrapper;
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

import java.time.LocalDate;
import java.util.Optional;

import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.common.util.MDCUtil.callId;
import static org.springframework.kafka.support.KafkaHeaders.KEY;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Component
@ConditionalOnProperty(value = "mottak.sender.domainevent.enabled", havingValue = "true")
public class KafkaInnsendingHendelseProdusent implements InnsendingHendelseProdusent {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaInnsendingHendelseProdusent.class);
    private static final Logger SECURE_LOG = LoggerFactory.getLogger("secureLogger");
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
    public void publiser(Fødselsnummer fnr, FordelResultat kvittering, String dialogId, Konvolutt konvolutt) {
        var callId = callId();
        var førsteBehandlingsdato = førsteInntektsmeldingDag(konvolutt).orElse(null);
        var h = new InnsendingHendelse(oppslag.aktørId(), fnr, kvittering.journalId(), callId, dialogId,
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
        kafkaOperations.send(melding)
                .whenComplete((input, exception) -> handleResponse(melding, input, exception));
    }

    private void handleResponse(Message<String> melding, SendResult<String, String> input, Throwable exception) {
        if (exception != null) {
            LOG.warn("Kunne ikke sende melding til topic {}, se secure logs for detaljer.", topic, exception);
            SECURE_LOG.warn("Kunne ikke sende melding til topic {}. Melding: {}", topic, melding, exception);
        } else {
            LOG.info("Sendte hendelse med offset {} OK", input.getRecordMetadata().offset());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[topic=" + topic + ", kafkaOperations=" + kafkaOperations + ", mapper="
                + mapper + "]";
    }
}
