package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.health.AbstractPingableHealthIndicator;

@Component
public class FPFordelHealthIndicator extends AbstractPingableHealthIndicator {
    public FPFordelHealthIndicator(FordelConnection connection) {
        super(connection);
    }
}
