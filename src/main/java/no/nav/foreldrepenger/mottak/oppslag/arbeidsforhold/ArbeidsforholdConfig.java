package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "arbeidsforhold")
public class ArbeidsforholdConfig {

    static final String ANSETTELSESPERIODE_TOM = "ansettelsesperiodeTom";
    static final String ANSETTELSESPERIODE_FOM = "ansettelsesperiodeFom";
    static final String SPORINGSINFORMASJON = "sporingsinformasjon";
    static final String HISTORIKK = "historikk";

    private final String baseUri;
    private final String pingPath;
    private final String arbeidsforholdPath;
    private final boolean log;

    @ConstructorBinding
    public ArbeidsforholdConfig(String baseUri, @DefaultValue("/ping") String pingPath,
            @DefaultValue("/v1/arbeidstaker/arbeidsforhold") String arbeidsforholdPath, boolean log) {
        this.baseUri = baseUri;
        this.pingPath = pingPath;
        this.arbeidsforholdPath = arbeidsforholdPath;
        this.log = log;
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

    public boolean isLog() {
        return log;
    }

}
