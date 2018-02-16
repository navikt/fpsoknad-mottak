package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.SøknadSendingsResultat;

@Service
public class DokmotJMSSender implements SøknadSender {

    private final JmsTemplate dokmotTemplate;
    private final DokmotXMLEnvelopeGenerator generator;

    private static final Logger LOG = LoggerFactory.getLogger(DokmotJMSSender.class);

    @Inject
    public DokmotJMSSender(JmsTemplate template, DokmotXMLEnvelopeGenerator generator) {
        this.dokmotTemplate = template;
        this.generator = generator;
    }

    public DokmotXMLEnvelopeGenerator getKonvoluttGenerator() {
        return generator;
    }

    @Override
    public SøknadSendingsResultat sendSøknad(Søknad søknad) {
        String xml = generator.toXML(søknad);
        dokmotTemplate.send(session -> {
            TextMessage msg = session.createTextMessage(xml);
            msg.setStringProperty("callId", "42");
            return msg;
        });
        return SøknadSendingsResultat.OK;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + dokmotTemplate + ", generator=" + generator + "]";
    }

}
