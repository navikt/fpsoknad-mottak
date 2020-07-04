package no.nav.foreldrepenger.mottak.http;

import java.net.URI;

public interface PingEndpointAware extends Pingable {

    URI pingEndpoint();

    String name();
}
