package no.nav.foreldrepenger.mottak.oppslag;

import java.net.URI;

import no.nav.foreldrepenger.mottak.util.URIUtil;

public class AbstractConfig {
    private final String baseUri;
    private final String pingPath;
    private final boolean enabled;

    public AbstractConfig(String baseUri, String pingPath) {
        this(baseUri, pingPath, true);

    }

    public AbstractConfig(String baseUri, String pingPath, boolean enabled) {
        this.baseUri = baseUri;
        this.pingPath = pingPath;
        this.enabled = enabled;
    }

    public URI pingEndpoint() {
        return URIUtil.uri(baseUri, pingPath);
    }

    public String getPingPath() {
        return pingPath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getBaseUri() {
        return baseUri;
    }
}
