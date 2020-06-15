package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.health.AbstractPingableHealthIndicator;

@Component
public class FordelHealthIndicator extends AbstractPingableHealthIndicator {
    public FordelHealthIndicator(FordelConnection connection) {
        super(connection);
    }
}
