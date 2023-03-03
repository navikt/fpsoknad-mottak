package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;

import static no.nav.foreldrepenger.mottak.util.URIUtil.queryParams;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

@ConfigurationProperties(prefix = "innsyn")
public class InnsynConfig extends AbstractConfig {
    private static final String DEFAULT_URI = "http://fpinfo/fpinfo";
    private static final String DEFAULT_PING_PATH = "internal/health/isAlive";

    private static final String DEFAULT_BASE_PATH = "api/v2/";
    private static final String SAKER = DEFAULT_BASE_PATH + "saker";
    private static final String ANNEN_PART_VEDTAK = DEFAULT_BASE_PATH + "annenPartVedtak";

    private static final String AKTOR_ID = "aktorId";

    @ConstructorBinding
    public InnsynConfig(@DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue("true") boolean enabled,
            @DefaultValue(DEFAULT_URI) URI baseUri) {
        super(baseUri, pingPath, enabled);
    }

    URI createLink(String l) {
        var base = getBaseUri().getScheme() + "://" + getBaseUri().getHost();
        var port = getBaseUri().getPort();
        if (port != -1) {
            base += ":" + port;
        }
        return URI.create(base + l);
    }

    URI sakerURI(String aktørId) {
        return uri(getBaseUri(), SAKER, queryParams(AKTOR_ID, aktørId));
    }

    URI annenPartVedtakURI() {
        return uri(getBaseUri(), ANNEN_PART_VEDTAK);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + getPingPath() + ", enabled=" + isEnabled() + ", uri=" + getBaseUri() + "]";
    }
}
