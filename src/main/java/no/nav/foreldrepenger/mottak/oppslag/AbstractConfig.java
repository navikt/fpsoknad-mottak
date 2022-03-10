package no.nav.foreldrepenger.mottak.oppslag;

import java.net.URI;

import no.nav.foreldrepenger.mottak.util.URIUtil;

public class AbstractConfig {
    private final URI baseUri;
    private final String pingPath;
    private final boolean enabled;

    public AbstractConfig(URI baseUri, String pingPath, boolean enabled) {
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

    public URI getBaseUri() {
        return baseUri;
    }

    public String name() {
        return baseUri.getHost();
    }

    @Override
    public String toString() {
        return "AbstractConfig{" +
            "baseUri=" + baseUri +
            ", pingPath='" + pingPath + '\'' +
            ", enabled=" + enabled +
            '}';
    }
}
