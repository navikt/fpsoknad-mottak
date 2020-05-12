package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.Optional;

@ConfigurationProperties(prefix = "fppdfgen")
@Configuration
public class PdfGeneratorConfig {
    private static final URI DEFAULT_URI = URI.create("http://fppdfgen.nais.preprod.local");
    private static final String DEFAULT_BASE_PATH = "/api/v1/genpdf/";
    private static final String DEFAULT_PING_PATH = DEFAULT_BASE_PATH + "is_alive";
    static final String ENGANGSSTØNAD = DEFAULT_BASE_PATH + "engangsstonad/engangsstonad";

    URI uri;
    String pingPath;
    String basePath;
    boolean enabled;

    public String getBasePath() {
        return Optional.ofNullable(basePath).orElse(DEFAULT_BASE_PATH);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPingPath() {
        return Optional.ofNullable(pingPath).orElse(DEFAULT_PING_PATH);
    }

    public URI getUri() {
        return Optional.ofNullable(uri).orElse(DEFAULT_URI);
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPingPath(String pingPath) {
        this.pingPath = pingPath;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + pingPath + ", enabled=" + enabled + ", uri=" + uri
            + ", basePath=" + basePath
            + "]";
    }
}
