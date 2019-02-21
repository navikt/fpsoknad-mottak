package no.nav.foreldrepenger.mottak.innsending.varsel;

import static no.nav.foreldrepenger.mottak.Constants.CALL_ID;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;

import java.time.LocalDateTime;

import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.felles.Person;

@Service
public class VarselSender {

    private final VarselConnection connection;
    private final VarselXMLGenerator generator;

    private static final Logger LOG = LoggerFactory.getLogger(VarselSender.class);

    public VarselSender(VarselConnection connection, VarselXMLGenerator generator) {
        this.connection = connection;
        this.generator = generator;
    }

    public void send(Person søker, LocalDateTime mottattDato) {
        if (connection.isEnabled()) {
            doSend(søker, mottattDato);
        }
        else {
            LOG.info("Sending av varsler er deaktivert, ingenting å sende");
        }
    }

    private void doSend(Person søker, LocalDateTime mottattDato) {
        connection.send(session -> {
            TextMessage msg = session.createTextMessage(generator.tilXml(søker, mottattDato));
            LOG.info("Legger melding på varselkø");
            msg.setStringProperty(CALL_ID, callId());
            return msg;
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", generator=" + generator + "]";
    }
}
