package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.health.AbstractPingableHealthIndicator;

@Component
public class PDLHealthIndicator extends AbstractPingableHealthIndicator {

    public PDLHealthIndicator(PDLConnection pingable) {
        super(pingable);
    }

}
