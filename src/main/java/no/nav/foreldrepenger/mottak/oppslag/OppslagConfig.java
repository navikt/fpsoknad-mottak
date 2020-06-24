package no.nav.foreldrepenger.mottak.oppslag;

import java.net.URI;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "oppslag", ignoreInvalidFields = true)
@Configuration
public class OppslagConfig {
    private static final String AKTØR = "oppslag/aktor";
    private static final String AKTØRFNR = "oppslag/aktorfnr";
    private static final String FNR = "oppslag/fnr";
    private static final String PERSON = "person";
    private static final String PERSONNAVN = "person/navn";
    private static final URI DEFAULT_BASE_URI = URI.create("http://fpsoknad-oppslag/api");
    private static final String DEFAULT_PING_PATH = "actuator/health/liveness";
    private String pingPath;
    private String aktørPath;
    private String aktørFnrPath;
    private String fnrPath;
    private String personPath;
    private String personNavnPath;
    private boolean enabled;
    private URI baseURI;

    public String getPersonNavnPath() {
        return Optional.ofNullable(personNavnPath).orElse(PERSONNAVN);
    }

    public void setPersonFnrPath(String personNavnPath) {
        this.personNavnPath = personNavnPath;
    }

    public String getPersonPath() {
        return Optional.ofNullable(personPath).orElse(PERSON);
    }

    public void setPersonPath(String personPath) {
        this.personPath = personPath;
    }

    public String getFnrPath() {
        return Optional.ofNullable(fnrPath).orElse(FNR);
    }

    public void setFnrPath(String fnrPath) {
        this.fnrPath = fnrPath;
    }

    public String getAktørPath() {
        return Optional.ofNullable(aktørPath).orElse(AKTØR);
    }

    public void setAktørPath(String aktørPath) {
        this.aktørPath = aktørPath;
    }

    public String getAktørFnrPath() {
        return Optional.ofNullable(aktørFnrPath).orElse(AKTØRFNR);
    }

    public void setAktørFnrPath(String aktørFnrPath) {
        this.aktørFnrPath = aktørFnrPath;
    }

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
