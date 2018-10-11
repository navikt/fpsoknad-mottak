package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.net.URI;

public interface Pingable {
    String ping();

    URI pingEndpoint();
}
