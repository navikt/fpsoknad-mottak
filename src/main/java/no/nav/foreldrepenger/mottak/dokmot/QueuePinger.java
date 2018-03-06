package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueuePinger {

    private static final Logger LOG = LoggerFactory.getLogger(QueuePinger.class);

    private final JmsTemplate dokmotTemplate;

    @Inject
    public QueuePinger(JmsTemplate dokmotTemplate) {
        this.dokmotTemplate = dokmotTemplate;
    }

    public void ping() {
        try {
            LOG.info("Pinging queue {}", dokmotTemplate.getDefaultDestinationName());
            dokmotTemplate.getConnectionFactory().createConnection().close();
        } catch (JMSException e) {
            throw new RemoteUnavailableException(e);
        }
    }
}
