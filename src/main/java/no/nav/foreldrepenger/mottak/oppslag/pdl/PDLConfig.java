package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "pdl")
public class PDLConfig extends AbstractConfig {
    private static final String DEFAULT_BASE_URI = "http://pdl-api.default/graphql";
    private static final String DEFAULT_PING_PATH = "/";
    private static final String DEFAULT_KONTONR_URI = "http://fpsoknad-oppslag/api/person/kontonr";
    private static final String DEFAULT_MAALFORM_URI = "http://fpsoknad-oppslag/api/person/maalform";

    private final URI kontonummerURI;
    private final URI maalformURI;

    @ConstructorBinding
    public PDLConfig(@DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue("true") boolean enabled,
            @DefaultValue(DEFAULT_BASE_URI) URI baseUri, @DefaultValue(DEFAULT_KONTONR_URI) URI kontonummerURI,
            @DefaultValue(DEFAULT_MAALFORM_URI) URI maalformUri) {
        super(baseUri, pingPath, enabled);
        this.kontonummerURI = kontonummerURI;
        this.maalformURI = maalformUri;
    }

    public URI getKontonummerURI() {
        return kontonummerURI;
    }

    public URI getMaalformURI() {
        return maalformURI;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + getPingPath() + ", enabled=" + isEnabled() + ", url=" + getBaseUri()
                + "]";
    }

}
