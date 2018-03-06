package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueuePinger {

    private final JmsTemplate dokmotTemplate;

    @Inject
    public QueuePinger(JmsTemplate dokmotTemplate) {
        this.dokmotTemplate = dokmotTemplate;
    }

    public void ping() {
        try {
            dokmotTemplate.getConnectionFactory().createConnection().close();
        } catch (JMSException e) {
            throw new RemoteUnavailableException(e);
        }
    }
}
