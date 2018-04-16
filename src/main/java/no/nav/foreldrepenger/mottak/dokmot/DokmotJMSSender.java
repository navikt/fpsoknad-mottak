package no.nav.foreldrepenger.mottak.dokmot;

import javax.jms.TextMessage;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.CorrelationIdGenerator;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;

@Service
public class DokmotJMSSender implements SøknadSender {

    private final DokmotConnection dokmotConnection;
    private final DokmotEngangsstønadXMLKonvoluttGenerator generator;
    private final CorrelationIdGenerator idGenerator;

    private final Counter sentCounter = Metrics.counter("fpsoknad.mottak.sent");

    private static final Logger LOG = LoggerFactory.getLogger(DokmotJMSSender.class);

    public DokmotJMSSender(DokmotConnection connection, DokmotEngangsstønadXMLKonvoluttGenerator generator,
            CorrelationIdGenerator callIdGenerator) {
        this.dokmotConnection = connection;
        this.generator = generator;
        this.idGenerator = callIdGenerator;
    }

    @Override
    public Kvittering sendSøknad(Søknad søknad) {
        if (dokmotConnection.isEnabled()) {
            LOG.info("Sender søknad til DOKMOT {}", søknad);
            String reference = idGenerator.getOrCreate();
            String xml = generator.toXML(søknad, reference);

            dokmotConnection.send(session -> {
                LOG.info("Sender SøknadsXML til DOKMOT {} : ({})", dokmotConnection.getQueueConfig().toString(),
                        generator.toSøknadsXML(søknad));
                TextMessage msg = session.createTextMessage(xml);
                msg.setStringProperty("callId", reference);
                return msg;
            });
            sentCounter.increment();
            return new Kvittering(reference);
        }
        LOG.info("Leveranse til DOKMOT er deaktivert, ingenting å sende");
        return Kvittering.IKKE_SENDT;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + dokmotConnection + ", generator=" + generator
                + ", callIdGenerator=" + idGenerator + "]";
    }

}
