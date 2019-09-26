package no.nav.foreldrepenger.mottak.health;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsyn.InnsynConnection;

@Component
public class FPInfoHealthIndicator extends AbstractPingableHealthIndicator {
    public FPInfoHealthIndicator(InnsynConnection connection) {
        super(connection);
    }
}
