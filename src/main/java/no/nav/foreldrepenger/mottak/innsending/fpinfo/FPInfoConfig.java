package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import java.net.URI;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@ConfigurationProperties(prefix = "fpinfo")
@Configuration
public class FPInfoConfig {

    private static final URI DEFAULT_URL = URI.create("http://fpinfo");
    private static final String DEFAULT_BASE_PATH = "fpinfo/api/dokumentforsendelse/";
    private static final String DEFAULT_PING_PATH = "fpinfo/internal/isReady";

    String pingPath;
    boolean enabled;
    URI url;
    String basePath;

    public String getBasePath() {
        return Optional.ofNullable(basePath).orElse(DEFAULT_BASE_PATH);
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

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
        return getClass().getSimpleName() + " [pingPath=" + pingPath + ", enabled=" + enabled + ", url=" + url
                + ", basePath=" + basePath
                + "]";
    }

}
