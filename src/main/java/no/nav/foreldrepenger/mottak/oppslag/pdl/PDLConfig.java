package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "pdl")
public class PDLConfig {
    private static final String DEFAULT_FØDSEL_MÅNEDER_TILBAKE = "40";
    private static final String DEFAULT_BASE_URI = "http://pdl-api.pdl/graphql";

    static final String NAVN_QUERY = "query-navn.graphql";
    static final String BARN_QUERY = "query-barn.graphql";
    static final String SØKER_QUERY = "query-person.graphql";
    static final String ANNEN_PART_QUERY = "query-annen-forelder.graphql";
    static final String IDENT_QUERY = "query-ident.graphql";

    private final int barnFoedtInnen;
    private final URI baseUri;

    public PDLConfig(@DefaultValue(DEFAULT_BASE_URI) URI baseUri,
                     @DefaultValue(DEFAULT_FØDSEL_MÅNEDER_TILBAKE) int barnFoedtInnen) {
        this.baseUri = baseUri;
        this.barnFoedtInnen = barnFoedtInnen;
    }

    int getBarnFødtInnen() {
        return barnFoedtInnen;
    }

    public URI getBaseUri() {
        return baseUri;
    }
}
