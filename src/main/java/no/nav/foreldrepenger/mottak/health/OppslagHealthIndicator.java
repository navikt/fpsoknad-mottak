package no.nav.foreldrepenger.mottak.health;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.oppslag.OppslagConnection;

@Component
public class OppslagHealthIndicator extends EnvironmentAwareHealthIndicator {

    public OppslagHealthIndicator(OppslagConnection connection) {
        super(connection);
    }
}
