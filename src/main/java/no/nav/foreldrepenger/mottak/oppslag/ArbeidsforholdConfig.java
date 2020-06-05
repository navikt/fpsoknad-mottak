package no.nav.foreldrepenger.mottak.oppslag;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "arbeidsforhold")
public class ArbeidsforholdConfig {

    private final String baseUri;
    private final String pingPath;
    private final String arbeidsforholdPath;

    @ConstructorBinding
    public ArbeidsforholdConfig(String baseUri, @DefaultValue("/ping") String pingPath,
            @DefaultValue("/v1/arbeidstaker/arbeidsforhold") String arbeidsforholdPath) {
        this.baseUri = baseUri;
        this.pingPath = pingPath;
        this.arbeidsforholdPath = arbeidsforholdPath;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public String getPingPath() {
        return pingPath;
    }

    public String getArbeidsforholdPath() {
        return arbeidsforholdPath;
    }

}
