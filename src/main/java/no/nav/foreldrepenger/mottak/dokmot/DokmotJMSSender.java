package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.domain.Pair;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.SøknadSendingsResultat;
import no.nav.foreldrepenger.mottak.domain.XMLKonvoluttGenerator;

@Service
public class DokmotJMSSender implements SøknadSender {

    private final JmsTemplate dokmotTemplate;
    private final XMLKonvoluttGenerator generator;
    private final CallIdGenerator callIdGenerator;

    private static final Logger LOG = LoggerFactory.getLogger(DokmotJMSSender.class);

    @Inject
    public DokmotJMSSender(JmsTemplate template, XMLKonvoluttGenerator generator, CallIdGenerator callIdGenerator) {
        this.dokmotTemplate = template;
        this.generator = generator;
        this.callIdGenerator = callIdGenerator;
    }

    public XMLKonvoluttGenerator getKonvoluttGenerator() {
        return generator;
    }

    @Override
    public SøknadSendingsResultat sendSøknad(Søknad søknad) {
        String xml = generator.toXML(søknad);
        dokmotTemplate.send(session -> {
            Pair<String, String> callId = callIdGenerator.generateCallId();
            TextMessage msg = session.createTextMessage(xml);
            msg.setStringProperty("callId", callId.getSecond());
            return msg;
        });
        return SøknadSendingsResultat.OK;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + dokmotTemplate + ", generator=" + generator + "]";
    }

}
