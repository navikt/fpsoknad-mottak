package no.nav.foreldrepenger.mottak.dokmot;

import java.util.UUID;

import javax.inject.Inject;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.SøknadSendingsResultat;
import no.nav.foreldrepenger.mottak.http.CallIdGenerator;

@Service
public class DokmotJMSSender implements SøknadSender {

    private final Counter dokmotSuccess = Metrics.counter("dokmot,send", "søknad", "success");
    private final Counter dokmotFailure = Metrics.counter("dokmot.send", "søknad", "failure");

    private final DokmotConnection dokmotConnection;
    private final DokmotEngangsstønadXMLKonvoluttGenerator generator;
    private final CallIdGenerator callIdGenerator;

    private static final Logger LOG = LoggerFactory.getLogger(DokmotJMSSender.class);

    @Inject
    public DokmotJMSSender(DokmotConnection connection, DokmotEngangsstønadXMLKonvoluttGenerator generator,
            CallIdGenerator callIdGenerator) {
        this.dokmotConnection = connection;
        this.generator = generator;
        this.callIdGenerator = callIdGenerator;
    }

    @Override
    public SøknadSendingsResultat sendSøknad(Søknad søknad) {
        String ref = UUID.randomUUID().toString();
        String xml = generator.toXML(søknad, ref);
        try {
            dokmotConnection.send(session -> {
                LOG.trace("Sending message to DOKMOT {} : ({})", dokmotConnection.getQueueConfig().toString(), xml);
                TextMessage msg = session.createTextMessage(xml);
                msg.setStringProperty("callId", callIdGenerator.getOrCreate());
                return msg;
            });
            dokmotSuccess.increment();
            return SøknadSendingsResultat.OK.withRef(ref);
        } catch (JmsException e) {
            LOG.warn("Unable to send to DOKMOT at {}", dokmotConnection.getQueueConfig().toString(), e);
            dokmotFailure.increment();
            throw (new DokmotQueueUnavailableException(e));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + dokmotConnection + ", generator=" + generator
                + ", callIdGenerator=" + callIdGenerator + "]";
    }

}
