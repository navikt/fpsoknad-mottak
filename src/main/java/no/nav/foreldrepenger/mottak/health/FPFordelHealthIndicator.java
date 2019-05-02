package no.nav.foreldrepenger.mottak.health;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelConnection;

@Component
public class FPFordelHealthIndicator extends EnvironmentAwareHealthIndicator {
    public FPFordelHealthIndicator(FPFordelConnection connection) {
        super(connection);
    }
}
