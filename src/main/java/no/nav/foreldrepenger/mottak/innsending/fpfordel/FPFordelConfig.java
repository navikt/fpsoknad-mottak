package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "fpfordel")
@Configuration
public class FPFordelConfig {

    boolean enabled = true;

    String uri = "http://fpfordel";
    String fpinfo = "http://fpinfo";

    public String getFpinfo() {
        return fpinfo;
    }

    public void setFpinfo(String fpinfo) {
        this.fpinfo = fpinfo;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + enabled + ", uri=" + uri + "]";
    }

}
