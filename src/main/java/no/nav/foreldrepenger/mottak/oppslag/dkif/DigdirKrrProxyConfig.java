package no.nav.foreldrepenger.mottak.oppslag.dkif;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

@ConfigurationProperties(prefix = "digdir")
public class DigdirKrrProxyConfig {

    private static final String DEFAULT_PERSON_PATH = "rest/v1/personer";
    private static final String DEFAULT_BASE_URI = "https://digdir-krr-proxy.intern.nav.no";
    private final String personPath;
    private final URI baseUri;

    public DigdirKrrProxyConfig(@DefaultValue(DEFAULT_BASE_URI) URI baseUri,
                                @DefaultValue(DEFAULT_PERSON_PATH) String personPath) {
        this.baseUri = baseUri;
        this.personPath = personPath;
    }

    URI kontaktUri() {
        return uri(getBaseUri(), personPath);
    }

    public URI getBaseUri() {
        return baseUri;
    }
}
