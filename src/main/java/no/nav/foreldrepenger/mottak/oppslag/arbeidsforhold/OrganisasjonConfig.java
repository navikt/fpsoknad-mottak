package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.util.UriBuilder;

@ConfigurationProperties(prefix = "organisasjon")
public class OrganisasjonConfig {

    private static final String DEFAULT_BASE_URI = "https://ereg-services.intern.nav.no/api/v2/organisasjon";
    private static final String V1_ORGANISASJON = "/{orgnr}";
    private static final String HISTORIKK = "historikk";
    private final String organisasjonPath;
    private final URI baseUri;

    public OrganisasjonConfig(@DefaultValue(DEFAULT_BASE_URI) URI baseUri,
            @DefaultValue(V1_ORGANISASJON) String organisasjonPath) {
        this.baseUri = baseUri;
        this.organisasjonPath = organisasjonPath;
    }

    URI getOrganisasjonURI(UriBuilder b, String orgnr) {
        return b.path(organisasjonPath)
                .queryParam(HISTORIKK, true)
                .build(orgnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[organisasjonPath=" + organisasjonPath + "]";
    }

    public URI getBaseUri() {
        return baseUri;
    }
}
