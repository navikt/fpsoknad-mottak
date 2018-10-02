package no.nav.foreldrepenger.mottak.http.health;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsyn.InnsynConnection;

@Component
public class FPInfoHealthIndicator extends EnvironmentAwareHealthIndicator {

    public FPInfoHealthIndicator(InnsynConnection connection) {
        super(connection);
    }
}
