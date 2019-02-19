package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.net.URI;

public interface PingEndpointAware extends Pingable {

    URI pingEndpoint();

    String name();
}
