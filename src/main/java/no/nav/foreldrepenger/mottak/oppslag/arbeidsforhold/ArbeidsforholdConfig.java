package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.util.UriBuilder;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "arbeidsforhold")
public class ArbeidsforholdConfig extends AbstractConfig {

    static final String FOM = "ansettelsesperiodeTom";
    static final String TOM = "ansettelsesperiodeFom";
    static final String SPORINGSINFORMASJON = "sporingsinformasjon";
    static final String HISTORIKK = "historikk";

    private final String arbeidsforholdPath;
    private boolean historikk;
    private boolean sporingsinformasjon;
    private final Duration tidTilbake;

    @ConstructorBinding
    public ArbeidsforholdConfig(String baseUri, @DefaultValue("/ping") String pingPath,
            @DefaultValue("/v1/arbeidstaker/arbeidsforhold") String arbeidsforholdPath,
            @DefaultValue("3years") Duration tidTilbake, boolean log) {
        super(baseUri, pingPath, log);
        this.arbeidsforholdPath = arbeidsforholdPath;
        this.tidTilbake = tidTilbake;
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

    public Duration getTidTilbake() {
        return tidTilbake;
    }

    URI getArbeidsforholdURI(UriBuilder b, LocalDate fom, LocalDate tom) {
        return b.path(getArbeidsforholdPath())
                .queryParam(HISTORIKK, isHistorikk())
                .queryParam(SPORINGSINFORMASJON, isSporingsinformasjon())
                .queryParam(FOM, fom.format(ISO_LOCAL_DATE))
                .queryParam(TOM, tom.format(ISO_LOCAL_DATE))
                .build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[arbeidsforholdPath=" + arbeidsforholdPath + ", historikk=" + historikk
                + ", sporingsinformasjon=" + sporingsinformasjon + ", pingEndpoint=" + pingEndpoint() + "tidTilbake="
                + tidTilbake + ", log="
                + isLog() + "]";
    }

}
