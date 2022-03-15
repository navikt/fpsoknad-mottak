package no.nav.foreldrepenger.mottak.oppslag.dkif;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "digdir")
public class DigdirKrrProxyConfig extends AbstractConfig {

    private static final String DEFAULT_PING_PATH = "rest/ping";
    private static final String DEFAULT_PERSON_PATH = "rest/v1/person";
    private static final String DEFAULT_BASE_URI = "https://digdir-krr-proxy.intern.nav.no";
    private final String personPath;

    @ConstructorBinding
    public DigdirKrrProxyConfig(@DefaultValue(DEFAULT_BASE_URI) URI baseUri,
                                @DefaultValue(DEFAULT_PING_PATH) String pingPath,
                                @DefaultValue(DEFAULT_PERSON_PATH) String personPath,
                                @DefaultValue("true") boolean enabled) {
        super(baseUri, pingPath, enabled);
        this.personPath = personPath;
    }

    URI kontaktUri() {
        return uri(getBaseUri(), personPath);
    }
}
