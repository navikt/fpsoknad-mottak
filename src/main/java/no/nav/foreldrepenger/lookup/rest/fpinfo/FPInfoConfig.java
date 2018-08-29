package no.nav.foreldrepenger.lookup.rest.fpinfo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "fpinfo")
@Configuration
public class FPInfoConfig {

    boolean enabled;
    String baseURL;

    public String getBaseURL() {
        return baseURL != null ? baseURL : "http://fpinfo";
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + enabled + ", baseURL=" + baseURL + "]";
    }
}
