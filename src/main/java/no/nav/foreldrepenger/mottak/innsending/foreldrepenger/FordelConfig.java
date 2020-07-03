package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

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
    URI baseUri;

    public String getBasePath() {
        return Optional.ofNullable(basePath).orElse(BASE_PATH);
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public URI getBaseUri() {
        return Optional.ofNullable(baseUri).orElse(DEFAULT_URI);
    }

    public void setBaseUri(URI baseUri) {
        this.baseUri = baseUri;
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

    public URI fordelEndpoint() {
        return uri(getBaseUri(), getBasePath());
    }

    public URI pingEndpoint() {
        return uri(getBaseUri(), getPingPath());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + enabled + ", baseUri=" + getBaseUri() + "]";
    }

}
