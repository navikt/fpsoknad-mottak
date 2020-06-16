package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.net.URI;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "fpfordel")
@Configuration
public class FordelConfig {

    public static final String PING_PATH = "fpfordel/internal/health/isAlive";
    public static final String BASE_PATH = "fpfordel/api/dokumentforsendelse";

    public static final URI DEFAULT_URI = URI.create("http://fpfordel");

    String pingPath;
    String basePath;

    boolean enabled;
    URI uri;

    public String getBasePath() {
        return Optional.ofNullable(basePath).orElse(BASE_PATH);
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public URI getUri() {
        return Optional.ofNullable(uri).orElse(DEFAULT_URI);
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPingPath() {
        return Optional.ofNullable(pingPath).orElse(PING_PATH);
    }

    public void setPingPath(String pingPath) {
        this.pingPath = pingPath;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + enabled + ", uri=" + uri + "]";
    }

}
