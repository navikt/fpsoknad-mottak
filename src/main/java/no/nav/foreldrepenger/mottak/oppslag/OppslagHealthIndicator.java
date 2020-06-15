package no.nav.foreldrepenger.mottak.oppslag;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.health.AbstractPingableHealthIndicator;

@Component
public class OppslagHealthIndicator extends AbstractPingableHealthIndicator {
    public OppslagHealthIndicator(OppslagConnection connection) {
        super(connection);
    }
}
