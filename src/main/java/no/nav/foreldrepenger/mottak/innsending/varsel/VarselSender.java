package no.nav.foreldrepenger.mottak.innsending.varsel;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Service;

import javax.jms.TextMessage;

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

    public void send(Person person) {
        connection.send(session -> {
            TextMessage msg = session.createTextMessage(generator.tilXml(person));
            LOG.info("Legger melding på varselkø");
            msg.setStringProperty(CALL_ID, callId());
            return msg;
        });
    }
}


//    private final DokmotConnection connection;
//    private final DokmotEngangsstønadXMLKonvoluttGenerator generator;
//
//    private static final Logger LOG = LoggerFactory.getLogger(DokmotJMSSender.class);
//
//    public DokmotJMSSender(DokmotConnection connection, DokmotEngangsstønadXMLKonvoluttGenerator generator) {
//        this.connection = connection;
//        this.generator = generator;
//    }
//
//    @Override
//    public Kvittering send(Søknad søknad, Person søker, Versjon versjon) {
//        if (connection.isEnabled()) {
//            connection.send(session -> {
//                TextMessage msg = session.createTextMessage(generator.tilXML(søknad, søker));
//                LOG.info("Sender SøknadsXML til DOKMOT");
//                msg.setStringProperty(CALL_ID, callId());
//                return msg;
//            });
//            return new Kvittering(LeveranseStatus.PÅ_VENT);
//        }
//        LOG.info("Leveranse til DOKMOT er deaktivert, ingenting å sende");
//        return IKKE_SENDT;
//    }
//
//    @Override
//    public Kvittering send(Endringssøknad endringsøknad, Person søker, Versjon versjon) {
//        throw new NotImplementedException("Sending av endringssøknad via DOKMOT er ikke støttet");
//    }
//
//    @Override
//    public Kvittering send(Ettersending ettersending, Person søker, Versjon versjon) {
//        throw new NotImplementedException("Ettersending for engangsstønad via DOKMOT er ikke støttet");
//    }
//
//    @Override
//    public String toString() {
//        return getClass().getSimpleName() + " [connection=" + connection + ", generator=" + generator + "]";
//    }
//
//}
