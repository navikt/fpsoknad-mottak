package no.nav.foreldrepenger.mottak.fpfordel;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "fp")
@Configuration
public class FPFordelConfig {

    boolean enabled;

    @NotNull
    String fordelUrl;

    public String getFordelUrl() {
        return fordelUrl;
    }

    public void setFordelUrl(String fordelUrl) {
        this.fordelUrl = fordelUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + enabled + ", fordelUrl=" + fordelUrl + "]";
    }

}
