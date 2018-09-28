package no.nav.foreldrepenger.mottak.http;

import java.net.URI;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "oppslag")
@Configuration
public class OppslagConfig {

    static final String AKTØR = "/oppslag/aktor";
    static final String AKTØRFNR = "/oppslag/aktorfnr";
    static final String FNR = "/oppslag/fnr";
    static final String PERSON = "/person";
    static final String ARBEID = "/arbeidsforhold";

    private static final URI DEFAULT_BASE_URI = URI.create("http://fpsoknad-oppslag/api");
    private static final String DEFAULT_PING_PATH = "/actuator/info";

    String pingPath;
    boolean enabled;
    URI baseURI;

    public URI getBaseURI() {
        return Optional.ofNullable(baseURI).orElse(DEFAULT_BASE_URI);
    }

    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI;
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
        return getClass().getSimpleName() + " [pingPath=" + pingPath + ", enabled=" + enabled + ", url=" + baseURI
                + "]";
    }
}
