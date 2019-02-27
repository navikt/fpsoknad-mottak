package no.nav.foreldrepenger.mottak.health;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.varsel.VarselConnection;

@Component
public class VarselHealthIndicator extends EnvironmentAwareHealthIndicator {

    public VarselHealthIndicator(VarselConnection connection) {
        super(connection);
    }
}
