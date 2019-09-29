package no.nav.foreldrepenger.mottak.health;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FordelConnection;

@Component
public class FPFordelHealthIndicator extends AbstractPingableHealthIndicator {
    public FPFordelHealthIndicator(FordelConnection connection) {
        super(connection);
    }
}
