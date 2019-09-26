package no.nav.foreldrepenger.mottak.health;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.oppslag.OppslagConnection;

@Component
public class OppslagHealthIndicator extends AbstractPingableHealthIndicator {
    public OppslagHealthIndicator(OppslagConnection connection) {
        super(connection);
    }
}
