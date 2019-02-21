package no.nav.foreldrepenger.mottak.innsending.varsel;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.jms.TextMessage;
import java.time.LocalDateTime;

import static no.nav.foreldrepenger.mottak.Constants.CALL_ID;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;

@Service
public class VarselSender {

    private final VarselConnection connection;
    private final VarselXMLGenerator generator;

    private static final Logger LOG = LoggerFactory.getLogger(VarselSender.class);

    public VarselSender(VarselConnection connection, VarselXMLGenerator generator) {
        this.connection = connection;
        this.generator = generator;
    }

    public void send(Person person, LocalDateTime mottattDato) {
        connection.send(session -> {
            TextMessage msg = session.createTextMessage(generator.tilXml(person, mottattDato));
            LOG.info("Legger melding på varselkø");
            msg.setStringProperty(CALL_ID, callId());
            return msg;
        });
    }
}
