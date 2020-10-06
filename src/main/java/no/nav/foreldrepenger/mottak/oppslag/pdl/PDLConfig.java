package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "pdl")
public class PDLConfig extends AbstractConfig {
    private static final String DEFAULT_BASE_URI = "http://fpsoknad-oppslag/api";
    private static final String DEFAULT_PING_PATH = "actuator/health/liveness";

    @ConstructorBinding
    public PDLConfig(@DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue("true") boolean enabled,
            @DefaultValue(DEFAULT_BASE_URI) String baseUri) {
        super(baseUri, pingPath, enabled);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + getPingPath() + ", enabled=" + isEnabled() + ", url=" + getBaseUri()
                + "]";
    }

}
