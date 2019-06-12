package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;

//@Service
@Profile({ PREPROD })
public class SøknadMottattMeldingKonsument {
    private static final Logger LOG = LoggerFactory.getLogger(SøknadMottattMeldingKonsument.class);

    @KafkaListener(topics = "#{'${mottak.sender.domainevent.topic}'}", groupId = "#{'${spring.kafka.consumer.group-id}'}")
    public void listen(String json, @Header(NAV_CALL_ID) String callId) {
        LOG.info("Mottok søknad {}", callId);
    }
}