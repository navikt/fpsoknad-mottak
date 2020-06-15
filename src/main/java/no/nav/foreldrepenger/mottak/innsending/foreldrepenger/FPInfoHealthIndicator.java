package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.health.AbstractPingableHealthIndicator;
import no.nav.foreldrepenger.mottak.innsyn.InnsynConnection;

@Component
public class FPInfoHealthIndicator extends AbstractPingableHealthIndicator {
    public FPInfoHealthIndicator(InnsynConnection connection) {
        super(connection);
    }
}
