package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.util.URIUtil.headers;
import static no.nav.foreldrepenger.mottak.util.URIUtil.queryParams;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.innsyn.v2.Saksnummer;
import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "innsyn")
public class InnsynConfig extends AbstractConfig {
    private static final String DEFAULT_URI = "http://fpinfo/fpinfo";
    private static final String DEFAULT_BASE_PATH = "api/dokumentforsendelse/";
    private static final String DEFAULT_PING_PATH = "internal/health/isAlive";
    private static final String SAK = DEFAULT_BASE_PATH + "sak";
    private static final String ANNENFORELDERPLAN = DEFAULT_BASE_PATH + "annenforelderplan";
    private static final String UTTAKSPLAN = DEFAULT_BASE_PATH + "uttaksplan";

    private static final String DEFAULT_BASE_V2_PATH = "api/v2/";
    private static final String SAKV2 = DEFAULT_BASE_V2_PATH + "saker";
    private static final String ANNEN_FORELDER_VEDTAKSPERIODER = DEFAULT_BASE_V2_PATH + "annenForeldersVedtaksperioder";

    private static final String AKTOR_ID = "aktorId";
    private static final String SAKSNUMMER = "saksnummer";
    private static final String ANNENPART = "aktorIdAnnenPart";
    private static final String BRUKER = "aktorIdBruker";

    private final String basePath;

    @ConstructorBinding
    public InnsynConfig(@DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue("true") boolean enabled,
            @DefaultValue(DEFAULT_URI) URI baseUri,
            @DefaultValue(DEFAULT_BASE_PATH) String basePath) {
        super(baseUri, pingPath, enabled);
        this.basePath = basePath;
    }

    URI uttaksplanURI(Saksnummer saksnummer) {
        return uri(getBaseUri(), UTTAKSPLAN, queryParams(SAKSNUMMER, saksnummer.value()));
    }

    URI sakURI(String aktørId) {
        return uri(getBaseUri(), SAK, queryParams(AKTOR_ID, aktørId));
    }

    URI uttaksplanURI(AktørId aktørId, AktørId annenPart) {
        return uri(getBaseUri(), ANNENFORELDERPLAN,
                queryParams(ANNENPART, annenPart.value(), BRUKER, aktørId.value()));
    }

    URI createLink(String l) {
        var base = getBaseUri().getScheme() + "://" + getBaseUri().getHost();
        var port = getBaseUri().getPort();
        if (port != -1) {
            base += ":" + port;
        }
        return URI.create(base + l);
    }

    URI sakV2URI(String aktørId) {
        return uri(getBaseUri(), SAKV2, queryParams(AKTOR_ID, aktørId));
    }

    URI annenPartsVedtaksperioderURI(String søker, String annenForelder, String barn) {
        var headers = headers();
        headers.add("sokersAktorId", søker);
        headers.add("annenPartAktorId", annenForelder);
        headers.add("barnAktorId", barn);
        return uri(getBaseUri(), ANNEN_FORELDER_VEDTAKSPERIODER, headers);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + getPingPath() + ", enabled=" + isEnabled() + ", uri=" + getBaseUri()
                + ", basePath=" + basePath + "]";
    }
}
