package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.mottak.domain.CorrelationIdGenerator;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.SøknadSendingsResultat;

@Service
public class DokmotJMSSender implements SøknadSender {

    private final Counter dokmotSuccess = Metrics.counter("dokmot,send", "søknad", "success");
    private final Counter dokmotFailure = Metrics.counter("dokmot.send", "søknad", "failure");

    private final DokmotConnection dokmotConnection;
    private final DokmotEngangsstønadXMLKonvoluttGenerator generator;
    private final CorrelationIdGenerator idGenerator;

    private static final Logger LOG = LoggerFactory.getLogger(DokmotJMSSender.class);

    @Inject
    public DokmotJMSSender(DokmotConnection connection, DokmotEngangsstønadXMLKonvoluttGenerator generator,
            CorrelationIdGenerator callIdGenerator) {
        this.dokmotConnection = connection;
        this.generator = generator;
        this.idGenerator = callIdGenerator;
    }

    @Override
    public SøknadSendingsResultat sendSøknad(Søknad søknad) {
        LOG.info("Sender søknad til DOKMOT {}", søknad);
        String reference = idGenerator.getOrCreate();
        String xml = generator.toXML(søknad, reference);
        try {
            dokmotConnection.send(session -> {
                LOG.trace("Sender XML til DOKMOT {} : ({})", dokmotConnection.getQueueConfig().toString(), xml);
                TextMessage msg = session.createTextMessage(xml);
                msg.setStringProperty("callId", reference);
                return msg;
            });
            dokmotSuccess.increment();
            return SøknadSendingsResultat.OK.withReference(reference);
        } catch (JmsException e) {
            LOG.warn("Feil ved sending til DOKMOT ({})", dokmotConnection.getQueueConfig().toString(), e);
            dokmotFailure.increment();
            throw (new DokmotQueueUnavailableException(e));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + dokmotConnection + ", generator=" + generator
                + ", callIdGenerator=" + idGenerator + "]";
    }

}
