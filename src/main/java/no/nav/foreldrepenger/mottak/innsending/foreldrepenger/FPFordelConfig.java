package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import java.net.URI;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@ConfigurationProperties(prefix = "fpfordel")
@Configuration
public class FPFordelConfig {

    private static final String PING_PATH = "fpfordel/internal/isReady";
    private static final String BASE_PATH = "fpfordel/api/dokumentforsendelse";

    private static final URI DEFAULT_URI = URI.create("http://fpfordel");

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

    private URI uriFra(String pathSegment) {
        return uriFra(pathSegment, new HttpHeaders());
    }

    private URI uriFra(String pathSegment, HttpHeaders queryParams) {
        return fromUri(getUri())
                .pathSegment(pathSegment)
                .queryParams(queryParams)
                .build()
                .toUri();
    }

    public URI getSendEndpoint() {
        return uriFra(getBasePath());
    }

    public URI getPingEndpoint() {
        return uriFra(getPingPath());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + enabled + ", uri=" + uri + "]";
    }

}
