package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.util.URIUtil.queryParams;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "innsyn")
public class InnsynConfig extends AbstractConfig {
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
    private final String basePath;

    @ConstructorBinding
    public InnsynConfig(@DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue("true") boolean enabled,
            @DefaultValue(DEFAULT_URI) URI baseUri,
            @DefaultValue(DEFAULT_BASE_PATH) String basePath) {
        super(baseUri, pingPath, enabled);
        this.basePath = basePath;
    }

    URI uttaksplanURI(String saksnummer) {
        return uri(getBaseUri(), UTTAKSPLAN, queryParams(SAKSNUMMER, saksnummer));
    }

    URI sakURI(String aktørId) {
        return uri(getBaseUri(), SAK, queryParams(AKTOR_ID, aktørId));
    }

    URI uttaksplanURI(AktørId aktørId, AktørId annenPart) {
        return uri(getBaseUri(), ANNENFORELDERPLAN,
                queryParams(ANNENPART, annenPart.value(), BRUKER, aktørId.value()));
    }

    URI createLink(String l) {
        return URI.create(getBaseUri() + l);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + getPingPath() + ", enabled=" + isEnabled() + ", uri=" + getBaseUri()
                + ", basePath=" + basePath + "]";
    }

}
