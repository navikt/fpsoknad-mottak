package no.nav.foreldrepenger.mottak.oppslag.sts;

import org.springframework.stereotype.Service;

@Service
public class STSSystemTokenTjeneste implements SystemTokenTjeneste {
    private final STSConnection connection;
    private SystemToken systemToken;

    public STSSystemTokenTjeneste(STSConnection connection) {
        this.connection = connection;
        connection.refresh();
    }

    @Override
    public SystemToken getSystemToken() {
        if (systemToken == null || systemToken.isExpired(connection.getSlack())) {
            systemToken = connection.refresh();
        }
        return systemToken;
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[connection=" + connection + ", systemToken=" + systemToken + "]";
    }

}
