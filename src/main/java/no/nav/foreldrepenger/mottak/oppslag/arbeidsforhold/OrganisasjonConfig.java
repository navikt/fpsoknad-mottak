package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static org.springframework.web.util.UriComponentsBuilder.newInstance;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.util.UriBuilder;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "organisasjon")
public class OrganisasjonConfig extends AbstractConfig {

    private static final String HISTORIKK = "historikk";
    private static final String V1_ORGANISASJON = "/v1/organisasjon/{orgnr}";
    private static final String NAV = "998004993";
    private final String organisasjonPath;

    @ConstructorBinding
    public OrganisasjonConfig(String baseUri,
            @DefaultValue(V1_ORGANISASJON) String organisasjonPath) {
        super(baseUri, pingPath(organisasjonPath));
        this.organisasjonPath = organisasjonPath;
    }

    private static String pingPath(String organisasjonPath) {
        return newInstance()
                .path(organisasjonPath)
                .build(NAV)
                .toString();
    }

    URI getOrganisasjonURI(UriBuilder b, String orgnr) {
        return b.path(organisasjonPath)
                .queryParam(HISTORIKK, true)
                .build(orgnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[organisasjonPath=" + organisasjonPath + ", pingEndpoint="
                + pingEndpoint() + "]";
    }

}
