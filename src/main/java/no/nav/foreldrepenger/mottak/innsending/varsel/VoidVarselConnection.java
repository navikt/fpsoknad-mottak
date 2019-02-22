package no.nav.foreldrepenger.mottak.innsending.varsel;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

@ConditionalOnMissingBean(VarselJMSConnection.class)
public class VoidVarselConnection implements VarselConnection {

    private static final Logger LOG = LoggerFactory.getLogger(VoidVarselConnection.class);

    @Override
    public URI pingEndpoint() {
        return URI.create("http://localhost");
    }

    @Override
    public String name() {
        return getClass().getSimpleName();
    }

    @Override
    public String ping() {
        return "Hello earthling";
    }

    @Override
    public void send(String xml) {
        LOG.info("Sender XML, liksom");

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
