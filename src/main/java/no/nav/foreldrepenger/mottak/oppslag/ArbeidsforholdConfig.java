package no.nav.foreldrepenger.mottak.oppslag;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "arbeidsforhold", ignoreInvalidFields = true)
@Configuration
public class ArbeidsforholdConfig {

    private final String baseUri;
    private final String pingPath;

    @ConstructorBinding
    public ArbeidsforholdConfig(String baseUri, @DefaultValue("/ping") String pingPath) {
        this.baseUri = baseUri;
        this.pingPath = pingPath;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public String getPingPath() {
        return pingPath;
    }

}
