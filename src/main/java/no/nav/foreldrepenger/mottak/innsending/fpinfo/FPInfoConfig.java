package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "fpinfo")
@Configuration
public class FPInfoConfig {

    boolean enabled;
    String url;

    public String getUrl() {
        return url != null ? url : "http://fpinfo";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + enabled + ", fpinfo=" + url + "]";
    }
}
