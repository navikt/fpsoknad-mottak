package no.nav.foreldrepenger.mottak.oppslag;

import java.net.URI;

import no.nav.foreldrepenger.mottak.util.URIUtil;

public class AbstractConfig {
    private final String baseUri;
    private final String pingPath;
    private final boolean log;

    public AbstractConfig(String baseUri, String pingPath, boolean log) {
        this.baseUri = baseUri;
        this.pingPath = pingPath;
        this.log = log;
    }

    public URI pingEndpoint() {
        return URIUtil.uri(baseUri, pingPath);
    }

    public boolean isLog() {
        return log;
    }

    public String getBaseUri() {
        return baseUri;
    }
}
