package no.nav.foreldrepenger.mottak.oppslag.sts;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.health.AbstractPingableHealthIndicator;

@Component
public class STSHealthIndicator extends AbstractPingableHealthIndicator {
    public STSHealthIndicator(STSConnection connection) {
        super(connection);
    }
}
