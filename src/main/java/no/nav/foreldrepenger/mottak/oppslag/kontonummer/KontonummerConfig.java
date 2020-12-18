package no.nav.foreldrepenger.mottak.oppslag.kontonummer;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "kontonummer")
public class KontonummerConfig extends AbstractConfig {
    private static final String DEFAULT_PING_PATH = "/";
    private static final String DEFAULT_BASE_URI = "http://fpsoknad-oppslag/api/person/kontonr";

    @ConstructorBinding
    public KontonummerConfig(@DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue("true") boolean enabled,
            @DefaultValue(DEFAULT_BASE_URI) URI baseUri) {
        super(baseUri, pingPath, enabled);
    }

}
