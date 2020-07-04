package no.nav.foreldrepenger.mottak.oppslag;

import static no.nav.foreldrepenger.mottak.Constants.FNR;
import static no.nav.foreldrepenger.mottak.util.URIUtil.queryParams;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;

@ConfigurationProperties(prefix = "oppslag")
public class OppslagConfig {
    private static final String DEFAULT_AKTØR_PATH = "oppslag/aktor";
    private static final String DEFAULT_AKTØRFNR_PATH = "oppslag/aktorfnr";
    private static final String DEFAULT_FNR_PATH = "oppslag/fnr";
    private static final String DEFAULT_PERSON_PATH = "person";
    private static final String DEFAULT_PERSONNAVN_PATH = "person/navn";
    private static final String DEFAULT_BASE_URI = "http://fpsoknad-oppslag/api";
    private static final String DEFAULT_PING_PATH = "actuator/health/liveness";
    private final String pingPath;
    private final String aktorPath;
    private final String aktorFnrPath;
    private final String fnrPath;
    private final String personPath;
    private final String personNavnPath;
    private final boolean enabled;
    private final URI baseUri;

    @ConstructorBinding
    public OppslagConfig(@DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue(DEFAULT_AKTØR_PATH) String aktorPath,
            @DefaultValue(DEFAULT_AKTØRFNR_PATH) String aktorFnrPath,
            @DefaultValue(DEFAULT_FNR_PATH) String fnrPath,
            @DefaultValue(DEFAULT_PERSON_PATH) String personPath,
            @DefaultValue(DEFAULT_PERSONNAVN_PATH) String personNavnPath,
            @DefaultValue("true") boolean enabled,
            @DefaultValue(DEFAULT_BASE_URI) URI baseUri) {
        this.pingPath = pingPath;
        this.aktorPath = aktorPath;
        this.aktorFnrPath = aktorFnrPath;
        this.fnrPath = fnrPath;
        this.personPath = personPath;
        this.personNavnPath = personNavnPath;
        this.enabled = enabled;
        this.baseUri = baseUri;
    }

    boolean isEnabled() {
        return enabled;
    }

    URI pingUri() {
        return uri(baseUri, pingPath);
    }

    URI aktørUri() {
        return uri(baseUri, aktorPath);
    }

    URI personUri() {
        return uri(baseUri, personPath);
    }

    URI aktørFnrUri(Fødselsnummer fnr) {
        return uri(baseUri, aktorFnrPath, queryParams(FNR, fnr.getFnr()));
    }

    URI navnUri(Fødselsnummer fnr) {
        return uri(baseUri, personNavnPath, queryParams(FNR, fnr.getFnr()));
    }

    URI fnrUri(AktørId aktørId) {
        return uri(baseUri, fnrPath, queryParams("aktorId", aktørId.getId()));
    }

    String name() {
        return baseUri.getHost();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + pingPath + ", enabled=" + enabled + ", url=" + baseUri
                + "]";
    }

}
