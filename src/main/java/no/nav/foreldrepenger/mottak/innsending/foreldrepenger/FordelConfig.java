package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

@ConfigurationProperties(prefix = "fpfordel")
public class FordelConfig extends AbstractConfig {

    private static final String DEFAULT_PING_PATH = "internal/health/isAlive";
    private static final String DEFAULT_BASE_PATH = "api/dokumentforsendelse";
    private static final String DEFAULT_POLLING_MAX = "10";

    private static final String DEFAULT_URI = "http://fpfordel/fpfordel";

    private final String basePath;
    private final int fpfordelMax;

    FordelConfig(URI uri) {
        this(uri, DEFAULT_PING_PATH, DEFAULT_BASE_PATH, Integer.parseInt(DEFAULT_POLLING_MAX), true);
    }

    @ConstructorBinding
    public FordelConfig(@DefaultValue(DEFAULT_URI) URI baseUri,
            @DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue(DEFAULT_BASE_PATH) String basePath,
            @DefaultValue(DEFAULT_POLLING_MAX) int fpfordelMax,
            @DefaultValue("true") boolean enabled) {
        super(baseUri, pingPath, enabled);
        this.basePath = basePath;
        this.fpfordelMax = fpfordelMax;
    }

    URI fordelEndpoint() {
        return uri(getBaseUri(), basePath);
    }

    public int maxPollingForsøk() {
        return fpfordelMax;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + isEnabled() + ", baseUri=" + getBaseUri() + "]";
    }

}
