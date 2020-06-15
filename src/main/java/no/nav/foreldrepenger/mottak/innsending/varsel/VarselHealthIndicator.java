package no.nav.foreldrepenger.mottak.innsending.varsel;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.health.AbstractPingableHealthIndicator;

@Component
public class VarselHealthIndicator extends AbstractPingableHealthIndicator {
    public VarselHealthIndicator(VarselConnection connection) {
        super(connection);
    }
}
