package no.nav.foreldrepenger.mottak.oppslag;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.health.AbstractPingableHealthIndicator;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;

@Component
public class OppslagHealthIndicator extends AbstractPingableHealthIndicator {
    public OppslagHealthIndicator(PDLConnection connection) {
        super(connection);
    }
}
