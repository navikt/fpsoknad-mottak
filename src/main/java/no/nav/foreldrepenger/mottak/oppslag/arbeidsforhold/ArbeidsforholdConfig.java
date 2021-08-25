package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import java.net.URI;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.util.UriBuilder;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "arbeidsforhold")
class ArbeidsforholdConfig extends AbstractConfig {

    private static final String DEFAULT_PING = "ping";
    private static final String TREÅR = "3y";
    private static final String V1_ARBEIDSTAKER_ARBEIDSFORHOLD = "/v1/arbeidstaker/arbeidsforhold";
    static final String FOM = "ansettelsesperiodeFom";
    static final String TOM = "ansettelsesperiodeTom";
    static final String SPORINGSINFORMASJON = "sporingsinformasjon";
    static final String HISTORIKK = "historikk";
    private static final String DEFAULT_BASE_URI = "http://must.be.set";

    private final String arbeidsforholdPath;
    private boolean historikk;
    private final boolean sporingsinformasjon;
    private final Period tidTilbake;

    @ConstructorBinding
    ArbeidsforholdConfig(@DefaultValue(DEFAULT_BASE_URI) URI baseUri, @DefaultValue(DEFAULT_PING) String pingPath,
            @DefaultValue(V1_ARBEIDSTAKER_ARBEIDSFORHOLD) String arbeidsforholdPath, @DefaultValue("true") boolean enabled,
            @DefaultValue(TREÅR) Period tidTilbake, boolean sporingsinformasjon) {
        super(baseUri, pingPath, enabled);
        this.arbeidsforholdPath = arbeidsforholdPath;
        this.tidTilbake = tidTilbake;
        this.sporingsinformasjon = sporingsinformasjon;
    }

    String getArbeidsforholdPath() {
        return arbeidsforholdPath;
    }

    boolean isHistorikk() {
        return historikk;
    }

    public void setHistorikk(boolean historikk) {
        this.historikk = historikk;
    }

    public boolean isSporingsinformasjon() {
        return sporingsinformasjon;
    }

    public Period getTidTilbake() {
        return tidTilbake;
    }

    URI getArbeidsforholdURI(UriBuilder b, LocalDate fom) {
        return b.path(getArbeidsforholdPath())
                .queryParam(HISTORIKK, isHistorikk())
                .queryParam(SPORINGSINFORMASJON, isSporingsinformasjon())
                .queryParam(FOM, fom.format(ISO_LOCAL_DATE))
                .build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[arbeidsforholdPath=" + arbeidsforholdPath + ", historikk=" + historikk
                + ", sporingsinformasjon=" + sporingsinformasjon + ", pingEndpoint=" + pingEndpoint() + "tidTilbake="
                + tidTilbake + "]";
    }

}
