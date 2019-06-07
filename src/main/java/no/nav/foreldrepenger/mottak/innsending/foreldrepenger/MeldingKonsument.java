package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Profile({ DEV, PREPROD })
public class MeldingKonsument {
    private static final Logger LOG = LoggerFactory.getLogger(MeldingKonsument.class);

    @KafkaListener(topics = "#{'${mottak.sender.domainevent.topic}'}", groupId = "#{'${spring.kafka.consumer.group-id}'}")
    public void listen(String json, Acknowledgment ack) {
        LOG.info("Mottok melding {}", json);
        ack.acknowledge();
    }
}