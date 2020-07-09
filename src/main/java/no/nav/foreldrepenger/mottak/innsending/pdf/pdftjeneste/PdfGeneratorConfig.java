package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.util.URIUtil;

@ConfigurationProperties(prefix = "fppdfgen")
public class PdfGeneratorConfig {
    private static final String DEFAULT_URI = "http://fppdfgen";
    private static final String DEFAULT_BASE_PATH = "/api/v1/genpdf/";
    private static final String DEFAULT_PING_PATH = DEFAULT_BASE_PATH + "is_alive";
    static final String SØKNAD = DEFAULT_BASE_PATH + "soknad-v2/soknad";

    private final URI uri;
    private final String pingPath;
    private final String basePath;
    private final boolean enabled;

    @ConstructorBinding
    public PdfGeneratorConfig(@DefaultValue(DEFAULT_URI) URI uri,
            @DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue(DEFAULT_BASE_PATH) String basePath,
            @DefaultValue("true") boolean enabled) {
        this.uri = uri;
        this.basePath = basePath;
        this.pingPath = pingPath;
        this.enabled = enabled;
    }

    String getBasePath() {
        return basePath;
    }

    boolean isEnabled() {
        return enabled;
    }

    String getPingPath() {
        return pingPath;
    }

    URI getUri() {
        return uri;
    }

    URI pingUri() {
        return URIUtil.uri(getUri(), getPingPath());
    }

    String name() {
        return getUri().getHost();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + pingPath + ", enabled=" + enabled + ", uri=" + uri
                + ", basePath=" + basePath
                + "]";
    }
}
