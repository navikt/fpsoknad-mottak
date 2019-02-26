package no.nav.foreldrepenger.mottak.innsending.varsel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VarselSender {

    private final VarselConnection connection;
    private final VarselXMLGenerator generator;

    private static final Logger LOG = LoggerFactory.getLogger(VarselSender.class);

    public VarselSender(VarselConnection connection, VarselXMLGenerator generator) {
        this.connection = connection;
        this.generator = generator;
    }

    public void varsle(Varsel varsel) {
        if (connection.isEnabled()) {
            connection.varsle(generator.tilXml(varsel));
        }
        else {
            LOG.info("Sending av varsler er deaktivert, ingenting å sende");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", generator=" + generator + "]";
    }
}
