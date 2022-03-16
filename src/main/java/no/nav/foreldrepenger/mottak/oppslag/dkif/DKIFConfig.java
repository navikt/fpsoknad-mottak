package no.nav.foreldrepenger.mottak.oppslag.dkif;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "dkif")
public class DKIFConfig extends AbstractConfig {

    private static final String DEFAULT_PING_PATH = "ping";
    private static final String DEFAULT_KONTAKT_PATH = "v1/personer/kontaktinformasjon";
    private static final String DEFAULT_BASE_URI = "http://dkif.default/api";
    private final String kontaktPath;

    @ConstructorBinding
    public DKIFConfig(@DefaultValue(DEFAULT_BASE_URI) URI baseUri,
            @DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue(DEFAULT_KONTAKT_PATH) String kontaktPath,
            @DefaultValue("true") boolean enabled) {
        super(baseUri, pingPath, enabled);
        this.kontaktPath = kontaktPath;
    }

    URI kontaktUri() {
        return uri(getBaseUri(), kontaktPath);
    }
}
