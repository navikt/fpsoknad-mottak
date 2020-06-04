package no.nav.foreldrepenger.mottak.health;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.oppslag.ArbeidsforholdConnection;

@Component
public class ArbeidsforholdHealthIndicator extends AbstractPingableHealthIndicator {
    public ArbeidsforholdHealthIndicator(ArbeidsforholdConnection connection) {
        super(connection);
    }
}
