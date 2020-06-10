package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.util.UriBuilder;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "organisasjon")
public class OrganisasjonConfig extends AbstractConfig {

    private final String organisasjonPath;

    @ConstructorBinding
    public OrganisasjonConfig(String baseUri, @DefaultValue("/ping") String pingPath,
            @DefaultValue("/v1/organisasjon/") String organisasjonPath, boolean log) {
        super(baseUri, pingPath, log);
        this.organisasjonPath = organisasjonPath;
    }

    URI getOrganisasjonURI(UriBuilder b) {
        return b.pathSegment(organisasjonPath)
                .build();
    }

}
