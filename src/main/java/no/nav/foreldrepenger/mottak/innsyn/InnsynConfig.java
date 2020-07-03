package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.util.URIUtil.queryParams;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.domain.AktørId;

@ConfigurationProperties(prefix = "innsyn")
public class InnsynConfig {
    private static final String DEFAULT_URI = "http://fpinfo";
    private static final String DEFAULT_BASE_PATH = "fpinfo/api/dokumentforsendelse/";
    private static final String DEFAULT_PING_PATH = "fpinfo/internal/health/isAlive";
    private static final String AKTOR_ID = "aktorId";
    private static final String SAKSNUMMER = "saksnummer";
    private static final String ANNENPART = "aktorIdAnnenPart";
    private static final String BRUKER = "aktorIdBruker";
    private static final String SAK = DEFAULT_BASE_PATH + "sak";
    private static final String ANNENFORELDERPLAN = DEFAULT_BASE_PATH + "annenforelderplan";
    private static final String UTTAKSPLAN = DEFAULT_BASE_PATH + "uttaksplan";
    private final String pingPath;
    private final boolean enabled;
    private final URI baseUri;
    private final String basePath;

    @ConstructorBinding
    public InnsynConfig(@DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue("true") boolean enabled,
            @DefaultValue(DEFAULT_URI) URI baseUri,
            @DefaultValue(DEFAULT_BASE_PATH) String basePath) {
        this.pingPath = pingPath;
        this.enabled = enabled;
        this.baseUri = baseUri;
        this.basePath = basePath;
    }

    String getBasePath() {
        return basePath;
    }

    URI getBaseUri() {
        return baseUri;
    }

    boolean isEnabled() {
        return enabled;
    }

    String getPingPath() {
        return pingPath;
    }

    URI uttaksplanURI(String saksnummer) {
        return uri(getBaseUri(), UTTAKSPLAN, queryParams(SAKSNUMMER, saksnummer));
    }

    URI sakURI(String aktørId) {
        return uri(getBaseUri(), SAK, queryParams(AKTOR_ID, aktørId));
    }

    URI pingURI() {
        return uri(getBaseUri(), getPingPath());
    }

    URI uttaksplanURI(AktørId aktørId, AktørId annenPart) {
        return uri(getBaseUri(), ANNENFORELDERPLAN,
                queryParams(ANNENPART, annenPart.getId(), BRUKER, aktørId.getId()));
    }

    URI createLink(String l) {
        return URI.create(getBaseUri() + l);
    }

    String name() {
        return getBaseUri().getHost();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + pingPath + ", enabled=" + enabled + ", uri=" + baseUri
                + ", basePath=" + basePath + "]";
    }

}
