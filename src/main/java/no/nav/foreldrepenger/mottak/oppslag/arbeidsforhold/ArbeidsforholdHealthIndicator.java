package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.health.AbstractPingableHealthIndicator;

@Component
public class ArbeidsforholdHealthIndicator extends AbstractPingableHealthIndicator {
    public ArbeidsforholdHealthIndicator(ArbeidsforholdConnection connection) {
        super(connection);
    }
}
