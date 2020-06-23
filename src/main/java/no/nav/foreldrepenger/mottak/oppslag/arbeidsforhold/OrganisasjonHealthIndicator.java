package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.health.AbstractPingableHealthIndicator;

@Component
public class OrganisasjonHealthIndicator extends AbstractPingableHealthIndicator {
    public OrganisasjonHealthIndicator(OrganisasjonConnection connection) {
        super(connection);
    }
}
