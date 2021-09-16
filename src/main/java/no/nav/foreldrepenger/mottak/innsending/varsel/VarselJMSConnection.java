package no.nav.foreldrepenger.mottak.innsending.varsel;

import static no.nav.foreldrepenger.common.util.Constants.CALL_ID;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.VARSEL_FAILED;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.VARSEL_SUCCESS;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;

import java.net.URI;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;

@Component
@ConditionalOnK8s
public class VarselJMSConnection implements VarselConnection {

    private static final Logger LOG = LoggerFactory.getLogger(VarselJMSConnection.class);

    private final JmsTemplate template;
    private final VarselConfig cfg;

    public VarselJMSConnection(JmsTemplate template, VarselConfig cfg) {
        this.template = template;
        this.cfg = cfg;
    }

    @Override
    public String ping() {
        try {
            template.getConnectionFactory().createConnection().close();
            return name() + " er i live på " + pingEndpoint();
        } catch (JMSException e) {
            LOG.warn("Kunne ikke pinge {}-kø ({})", name(), cfg.getURI(), e);
            throw new IllegalArgumentException("Kunne ikke pinge " + name() + "-kø", e);
        }
    }

    @Override
    public URI pingEndpoint() {
        return cfg.getURI();
    }

    private boolean isEnabled() {
        return cfg.isEnabled();
    }

    @Override
    public String name() {
        return "varseltjeneste";
    }

    @Override
    public void varsle(String xml) {
        if (isEnabled()) {
            LOG.info("Legger melding for varsel på {}-kø ({})", name(), cfg.getURI());
            try {
                template.send(session -> {
                    TextMessage msg = session.createTextMessage(xml);
                    msg.setStringProperty(CALL_ID, callId());
                    LOG.info("Varsel lagt på kø OK");
                    return msg;
                });
                VARSEL_SUCCESS.increment();
            } catch (JmsException e) {
                LOG.error("Feil ved sending av varsel til {}-kø ({})", name(), cfg.getURI(), e);
                VARSEL_FAILED.increment();
            }
        } else {
            LOG.info("Varsling er deaktivert");
        }

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", varselConfig=" + cfg.getURI() + "]";
    }

}
