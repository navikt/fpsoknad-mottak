package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.health.AbstractPingableHealthIndicator;
import no.nav.foreldrepenger.mottak.innsyn.InnsynConnection;

@Component
public class InfoHealthIndicator extends AbstractPingableHealthIndicator {
    public InfoHealthIndicator(InnsynConnection connection) {
        super(connection);
    }
}
