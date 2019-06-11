package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Profile({ PREPROD })
public class SøknadMottattMeldingKonsument {
    private static final Logger LOG = LoggerFactory.getLogger(SøknadMottattMeldingKonsument.class);

    @KafkaListener(topics = "#{'${mottak.sender.domainevent.topic}'}", groupId = "#{'${spring.kafka.consumer.group-id}'}")
    public void listen(String json) {
        LOG.info("Mottok melding");
    }
}