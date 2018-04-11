package no.nav.foreldrepenger.mottak.dokmot;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

public class DokmotConnection {

    private static final Logger LOG = LoggerFactory.getLogger(DokmotConnection.class);

    private final Counter dokmotSuccess = Metrics.counter("dokmot,send", "søknad", "success");
    private final Counter dokmotFailure = Metrics.counter("dokmot.send", "søknad", "failure");

    private final JmsTemplate template;
    private final DokmotQueueConfig queueConfig;

    public DokmotConnection(JmsTemplate template, DokmotQueueConfig queueConfig) {
        this.template = template;
        this.queueConfig = queueConfig;
    }

    public void ping() {
        LOG.info("Pinging DOKMOT queue {}", queueConfig);
        try {
            template.getConnectionFactory().createConnection().close();
        } catch (JMSException e) {
            LOG.warn("Unable to send to DOKMOT queue at {}", queueConfig);
            throw new DokmotQueueUnavailableException(e, queueConfig);
        }
    }

    public void send(MessageCreator msg) {
        try {
            template.send(msg);
            dokmotSuccess.increment();
        } catch (JmsException e) {
            LOG.warn("Unable to send to DOKMOT queue at {}", queueConfig);
            dokmotFailure.increment();
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
        return getClass().getSimpleName() + " [template=" + template + ", queueConfig=" + queueConfig + "]";
    }
}
