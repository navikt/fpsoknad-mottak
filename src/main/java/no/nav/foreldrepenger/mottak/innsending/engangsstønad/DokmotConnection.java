package no.nav.foreldrepenger.mottak.innsending.engangsstønad;

import static no.nav.foreldrepenger.mottak.util.CounterRegistry.DOKMOT_FAILURE;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.DOKMOT_SUKSESS;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
@Qualifier("dokmotConnection")
public class DokmotConnection {

    private static final Logger LOG = LoggerFactory.getLogger(DokmotConnection.class);

    private final JmsTemplate template;
    private final DokmotQueueConfig queueConfig;

    public DokmotConnection(@Qualifier("dokmotTemplate") JmsTemplate template, DokmotQueueConfig queueConfig) {
        this.template = template;
        this.queueConfig = queueConfig;
    }

    public void ping() {
        LOG.info("Pinger {}", queueConfig.loggable());
        try {
            template.getConnectionFactory().createConnection().close();
        } catch (JMSException e) {
            LOG.warn("Kunne ikke sende til DOKMOT kø {}", queueConfig.loggable());
            throw new DokmotQueueUnavailableException(e, queueConfig);
        }
    }

    public void send(MessageCreator msg) {
        try {
            template.send(msg);
            DOKMOT_SUKSESS.increment();
        } catch (JmsException e) {
            LOG.warn("Feil ved sending til DOKMOT {}", queueConfig.loggable(), e);
            DOKMOT_FAILURE.increment();
            throw new DokmotQueueUnavailableException(e, queueConfig);
        }
    }

    public DokmotQueueConfig getQueueConfig() {
        return queueConfig;
    }

    public boolean isEnabled() {
        return queueConfig.isEnabled();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", queueConfig=" + queueConfig.loggable() + "]";
    }
}
