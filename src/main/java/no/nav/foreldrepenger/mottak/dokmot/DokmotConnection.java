package no.nav.foreldrepenger.mottak.dokmot;

import org.springframework.jms.core.JmsTemplate;

public class DokmotConnection {

    private final JmsTemplate template;
    private final DokmotQueueConfig queueConfig;

    public DokmotConnection(JmsTemplate template, DokmotQueueConfig queueConfig) {
        this.template = template;
        this.queueConfig = queueConfig;
    }

    public JmsTemplate getTemplate() {
        return template;
    }

    public DokmotQueueConfig getQueueConfig() {
        return queueConfig;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", queueConfig=" + queueConfig + "]";
    }
}
