package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "fpfordel")
public class FordelConfig {

    private static final String DEFAULT_PING_PATH = "fpfordel/internal/health/isAlive";
    private static final String DEFAULT_BASE_PATH = "fpfordel/api/dokumentforsendelse";

    private static final String DEFAULT_URI = "http://fpfordel";

    private final String pingPath;
    private final String basePath;
    private final boolean enabled;
    private final URI baseUri;

    FordelConfig(URI uri) {
        this(uri, DEFAULT_PING_PATH, DEFAULT_BASE_PATH, true);
    }

    @ConstructorBinding
    public FordelConfig(@DefaultValue(DEFAULT_URI) URI baseUri,
            @DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue(DEFAULT_BASE_PATH) String basePath,
            @DefaultValue("true") boolean enabled) {
        this.pingPath = pingPath;
        this.basePath = basePath;
        this.enabled = enabled;
        this.baseUri = baseUri;
    }

    boolean isEnabled() {
        return enabled;
    }

    URI fordelEndpoint() {
        return uri(baseUri, basePath);
    }

    URI pingEndpoint() {
        return uri(baseUri, pingPath);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + enabled + ", baseUri=" + baseUri + "]";
    }

}
