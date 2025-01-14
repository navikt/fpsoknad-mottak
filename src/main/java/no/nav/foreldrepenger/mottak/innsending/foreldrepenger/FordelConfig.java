package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "fpfordel")
public class FordelConfig {

    private static final String DEFAULT_BASE_PATH = "api/dokumentforsendelse";
    private static final String DEFAULT_POLLING_MAX = "10";

    private static final String DEFAULT_URI = "http://fpfordel.teamforeldrepenger.svc.nais.local/fpfordel";

    private final String basePath;
    private final int fpfordelMax;
    private final URI baseUri;

    FordelConfig(URI uri) {
        this(uri, DEFAULT_BASE_PATH, Integer.parseInt(DEFAULT_POLLING_MAX));
    }

    @ConstructorBinding
    public FordelConfig(@DefaultValue(DEFAULT_URI) URI baseUri,
            @DefaultValue(DEFAULT_BASE_PATH) String basePath,
            @DefaultValue(DEFAULT_POLLING_MAX) int fpfordelMax) {
        this.baseUri = baseUri;
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
        return getClass().getSimpleName() + " [baseUri=" + getBaseUri() + "]";
    }

    public URI getBaseUri() {
        return baseUri;
    }
}
