package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.util.UriBuilder;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "arbeidsforhold")
public class ArbeidsforholdConfig extends AbstractConfig {

    static final String ANSETTELSESPERIODE_TOM = "ansettelsesperiodeTom";
    static final String ANSETTELSESPERIODE_FOM = "ansettelsesperiodeFom";
    static final String SPORINGSINFORMASJON = "sporingsinformasjon";
    static final String HISTORIKK = "historikk";

    private final String arbeidsforholdPath;
    private boolean historikk;
    private boolean sporingsinformasjon;

    @ConstructorBinding
    public ArbeidsforholdConfig(String baseUri, @DefaultValue("/ping") String pingPath,
            @DefaultValue("/v1/arbeidstaker/arbeidsforhold") String arbeidsforholdPath, boolean log) {
        super(baseUri, pingPath, log);
        this.arbeidsforholdPath = arbeidsforholdPath;
    }

    public String getArbeidsforholdPath() {
        return arbeidsforholdPath;
    }

    public boolean isHistorikk() {
        return historikk;
    }

    public void setHistorikk(boolean historikk) {
        this.historikk = historikk;
    }

    public boolean isSporingsinformasjon() {
        return sporingsinformasjon;
    }

    public void setSporingsinformasjon(boolean sporingsinformasjon) {
        this.sporingsinformasjon = sporingsinformasjon;
    }

    URI getArbeidsforholdURI(UriBuilder b) {
        return b.path(getArbeidsforholdPath())
                .queryParam(HISTORIKK, isHistorikk())
                .queryParam(SPORINGSINFORMASJON, isSporingsinformasjon())
                .build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[arbeidsforholdPath=" + arbeidsforholdPath + ", historikk=" + historikk
                + ", sporingsinformasjon=" + sporingsinformasjon + ", pingEndpoint=" + pingEndpoint + ", isLog()="
                + isLog() + "]";
    }

}
