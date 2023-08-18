package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import java.net.URI;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.util.UriBuilder;

@ConfigurationProperties(prefix = "arbeidsforhold")
public class ArbeidsforholdConfig {

    private static final String DEFAULT_BASE_URI = "https://aareg-services.intern.nav.no/api";
    private static final String V1_ARBEIDSTAKER_ARBEIDSFORHOLD = "/v1/arbeidstaker/arbeidsforhold";

    private static final String TREÅR = "3y";
    private static final String FOM_NAVN = "ansettelsesperiodeFom";
    private static final String SPORINGSINFORMASJON_NAVN = "sporingsinformasjon";
    private static final String HISTORIKK_NAVN = "historikk";
    private static final boolean ER_HISTORIKK_AKTIVE = false;

    private final Period tidTilbake;
    private final String arbeidsforholdPath;
    private final boolean sporingsinformasjon;
    private final URI baseUri;

    public ArbeidsforholdConfig(@DefaultValue(DEFAULT_BASE_URI) URI baseUri,
                                @DefaultValue(V1_ARBEIDSTAKER_ARBEIDSFORHOLD) String arbeidsforholdPath,
                                @DefaultValue(TREÅR) Period tidTilbake,
                                @DefaultValue("true") boolean sporingsinformasjon) {
        this.baseUri = baseUri;
        this.tidTilbake = tidTilbake;
        this.arbeidsforholdPath = arbeidsforholdPath;
        this.sporingsinformasjon = sporingsinformasjon;
    }

    public String getArbeidsforholdPath() {
        return arbeidsforholdPath;
    }

    public boolean isSporingsinformasjon() {
        return sporingsinformasjon;
    }

    public Period getTidTilbake() {
        return tidTilbake;
    }

    URI getArbeidsforholdURI(UriBuilder b, LocalDate fom) {
        return b.path(getArbeidsforholdPath())
            .queryParam(HISTORIKK_NAVN, ER_HISTORIKK_AKTIVE)
            .queryParam(SPORINGSINFORMASJON_NAVN, isSporingsinformasjon())
            .queryParam(FOM_NAVN, fom.format(ISO_LOCAL_DATE))
            .build();
    }

    @Override
    public String toString() {
        return "ArbeidsforholdConfig{" +
            "arbeidsforholdPath='" + arbeidsforholdPath + '\'' +
            ", sporingsinformasjon=" + sporingsinformasjon +
            ", tidTilbake=" + tidTilbake +
            "} " + super.toString();
    }

    public URI getBaseUri() {
        return baseUri;
    }
}
