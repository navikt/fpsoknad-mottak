package no.nav.foreldrepenger.mottak.oppslag;

import java.net.URI;

import no.nav.foreldrepenger.mottak.util.URIUtil;

public class AbstractConfig {
    private final String baseUri;
    private final String pingPath;

    public AbstractConfig(String baseUri, String pingPath) {
        this.baseUri = baseUri;
        this.pingPath = pingPath;
    }

    public URI pingEndpoint() {
        return URIUtil.uri(baseUri, pingPath);
    }

    public String getBaseUri() {
        return baseUri;
    }
}
