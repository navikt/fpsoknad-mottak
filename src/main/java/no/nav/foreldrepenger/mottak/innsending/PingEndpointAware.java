package no.nav.foreldrepenger.mottak.innsending;

import java.net.URI;

public interface PingEndpointAware extends Pingable {

    URI pingEndpoint();

    String name();
}
