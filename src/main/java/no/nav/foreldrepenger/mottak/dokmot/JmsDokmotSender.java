package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;
import javax.jms.TextMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.SøknadSendingsResultat;

@Service
public class JmsDokmotSender implements SøknadSender {

    private final JmsTemplate dokmotTemplate;
    private final XMLEnvelopeGenerator generator;

    @Inject
    public JmsDokmotSender(JmsTemplate template, XMLEnvelopeGenerator generator) {
        this.dokmotTemplate = template;
        this.generator = generator;
    }

    @Override
    public SøknadSendingsResultat sendSøknad(Søknad søknad) {
        String xml = generator.toXML(søknad);
        dokmotTemplate.send(textMessage(xml, "42")); // TODO
        return SøknadSendingsResultat.OK;
    }

    private static MessageCreator textMessage(final String xml, final String callId) {
        return session -> {
            TextMessage msg = session.createTextMessage(xml);
            msg.setStringProperty("callId", callId);
            return msg;
        };
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + dokmotTemplate + ", generator=" + generator + "]";
    }

}
