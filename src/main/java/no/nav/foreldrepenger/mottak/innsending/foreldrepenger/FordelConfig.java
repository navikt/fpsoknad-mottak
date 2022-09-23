package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "fpfordel")
public class FordelConfig extends AbstractConfig {

    private static final String DEFAULT_PING_PATH = "internal/health/isAlive";
    private static final String DEFAULT_BASE_PATH = "api/dokumentforsendelse";

    private static final String DEFAULT_URI = "http://fpfordel/fpfordel";

    private final String basePath;

    FordelConfig(URI uri) {
        this(uri, DEFAULT_PING_PATH, DEFAULT_BASE_PATH, true);
    }

    @ConstructorBinding
    public FordelConfig(@DefaultValue(DEFAULT_URI) URI baseUri,
            @DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue(DEFAULT_BASE_PATH) String basePath,
            @DefaultValue("true") boolean enabled) {
        super(baseUri, pingPath, enabled);
        this.basePath = basePath;
    }

    URI fordelEndpoint() {
        return uri(getBaseUri(), basePath);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + isEnabled() + ", baseUri=" + getBaseUri() + "]";
    }

}
