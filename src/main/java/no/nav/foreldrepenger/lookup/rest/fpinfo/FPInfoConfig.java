package no.nav.foreldrepenger.lookup.rest.fpinfo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "fpinfo")
@Configuration
public class FPInfoConfig {

    boolean enabled;
    String fpinfo;

    public String getFpinfo() {
        return fpinfo != null ? fpinfo : "http://fpinfo/fpinfo/api/dokumentforsendelse";
    }

    public void setFpinfo(String fpinfo) {
        this.fpinfo = fpinfo;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + enabled + ", fpinfo=" + fpinfo + "]";
    }
}
