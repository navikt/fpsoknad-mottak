package no.nav.foreldrepenger.mottak.innsyn;

import java.net.URI;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "innsyn")
@Configuration
public class InnsynConfig {
    private static final URI DEFAULT_URI = URI.create("http://fpinfo");
    private static final String DEFAULT_BASE_PATH = "fpinfo/api/dokumentforsendelse/";
    private static final String DEFAULT_PING_PATH = "fpinfo/internal/isReady";
    static final String AKTOR_ID = "aktorId";
    static final String SAKSNUMMER = "saksnummer";
    static final String BEHANDLING_ID = "behandlingId";
    static final String SØKNAD = DEFAULT_BASE_PATH + "søknad";
    static final String SAK = DEFAULT_BASE_PATH + "sak";
    static final String UTTAKSPLAN = DEFAULT_BASE_PATH + "uttaksplan";
    static final String BEHANDLING = DEFAULT_BASE_PATH + "behandling";
    String pingPath;
    boolean enabled;
    URI uri;
    String basePath;

    public String getBasePath() {
        return Optional.ofNullable(basePath).orElse(DEFAULT_BASE_PATH);
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
        return Optional.ofNullable(pingPath).orElse(DEFAULT_PING_PATH);
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
