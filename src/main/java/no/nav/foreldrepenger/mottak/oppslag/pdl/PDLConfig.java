package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "pdl")
public class PDLConfig extends AbstractConfig {
    private static final String DEFAULT_BASE_URI = "http://fpsoknad-oppslag/api";
    private static final String DEFAULT_PING_PATH = "actuator/health/liveness";
    private static final String DEFAULT_KONTONR_URI = "http://fpsoknad-oppslag/api/person/kontonr";
    private final URI kontonummerURI;

    @ConstructorBinding
    public PDLConfig(@DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue("true") boolean enabled,
            @DefaultValue(DEFAULT_BASE_URI) String baseUri, @DefaultValue(DEFAULT_KONTONR_URI) URI kontonummerURI) {
        super(baseUri, pingPath, enabled);
        this.kontonummerURI = kontonummerURI;
    }

    public URI getKontonummerURI() {
        return kontonummerURI;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + getPingPath() + ", enabled=" + isEnabled() + ", url=" + getBaseUri()
                + "]";
    }

}
