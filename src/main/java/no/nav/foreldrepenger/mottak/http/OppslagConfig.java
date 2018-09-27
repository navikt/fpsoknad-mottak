package no.nav.foreldrepenger.mottak.http;

import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import java.net.URI;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@ConfigurationProperties(prefix = "oppslag")
@Configuration
public class OppslagConfig {

    private static final URI DEFAULT_URL = URI.create("http://fpsoknad-oppslag/api");
    private static final String DEFAULT_PING_PATH = "/actuator/info";

    String pingPath;
    boolean enabled;
    URI url;

    public URI getUrl() {
        return Optional.ofNullable(url).orElse(DEFAULT_URL);
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPingPath() {
        return Optional.ofNullable(pingPath).orElse(DEFAULT_PING_PATH);
    }

    public void setPingPath(String pingPath) {
        this.pingPath = pingPath;
    }

    private URI uriFra(String pathSegment) {
        return uriFra(pathSegment, new HttpHeaders());
    }

    private URI uriFra(String pathSegment, HttpHeaders queryParams) {
        return fromUri(getUrl())
                .pathSegment(pathSegment)
                .queryParams(queryParams)
                .build()
                .toUri();
    }

    public URI pingEndpoint() {
        return uriFra(getPingPath());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + pingPath + ", enabled=" + enabled + ", url=" + url + "]";
    }
}
