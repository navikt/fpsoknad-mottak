package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "pdl")
public class PDLConfig extends AbstractConfig {
    private static final String DEFAULT_DØD_BARN_MÅNEDER_TILBAKE = "4";
    private static final String DEFAULT_FØDSEL_MÅNEDER_TILBAKE = "24";
    private static final String DEFAULT_BASE_URI = "http://pdl-api.pdl/graphql";
    private static final String DEFAULT_PING_PATH = "/";

    static final String NAVN_QUERY = "query-navn.graphql";
    static final String BARN_QUERY = "query-barn.graphql";
    static final String SØKER_QUERY = "query-person.graphql";
    static final String ANNEN_PART_QUERY = "query-annen-forelder.graphql";
    static final String IDENT_QUERY = "query-ident.graphql";

    private final int barnFoedtInnen;
    private final int doedSjekk;

    @ConstructorBinding
    public PDLConfig(@DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue("true") boolean enabled,
            @DefaultValue(DEFAULT_BASE_URI) URI baseUri,
            @DefaultValue(DEFAULT_FØDSEL_MÅNEDER_TILBAKE) int barnFoedtInnen,
            @DefaultValue(DEFAULT_DØD_BARN_MÅNEDER_TILBAKE) int doedSjekk) {
        super(baseUri, pingPath, enabled);
        this.barnFoedtInnen = barnFoedtInnen;
        this.doedSjekk = doedSjekk;
    }

    int getBarnFødtInnen() {
        return barnFoedtInnen;
    }

    int getDødSjekk() {
        return doedSjekk;
    }

}
