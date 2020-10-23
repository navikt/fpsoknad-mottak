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
    private static final String DEFAULT_BASE_URI = "http://pdl-api.default/graphql";
    private static final String DEFAULT_PING_PATH = "/";
    private static final String DEFAULT_KONTONR_URI = "http://fpsoknad-oppslag/api/person/kontonr";
    private static final String DEFAULT_MAALFORM_URI = "http://fpsoknad-oppslag/api/person/maalform";

    private static final String NAVN_QUERY = "query-navn.graphql";
    private static final String BARN_QUERY = "query-barn.graphql";
    private static final String SØKER_QUERY = "query-person.graphql";
    private static final String ANNEN_PART_QUERY = "query-annen-forelder.graphql";
    private static final String IDENT_QUERY = "query-ident.graphql";

    private final URI kontonummerURI;
    private final URI maalformURI;
    private final int barnFoedtInnen;
    private final int doedSjekk;

    @ConstructorBinding
    public PDLConfig(@DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue("true") boolean enabled,
            @DefaultValue(DEFAULT_BASE_URI) URI baseUri, @DefaultValue(DEFAULT_KONTONR_URI) URI kontonummerURI,
            @DefaultValue(DEFAULT_MAALFORM_URI) URI maalformUri,
            @DefaultValue(DEFAULT_FØDSEL_MÅNEDER_TILBAKE) int barnFoedtInnen,
            @DefaultValue(DEFAULT_DØD_BARN_MÅNEDER_TILBAKE) int doedSjekk) {
        super(baseUri, pingPath, enabled);
        this.kontonummerURI = kontonummerURI;
        this.maalformURI = maalformUri;
        this.barnFoedtInnen = barnFoedtInnen;
        this.doedSjekk = doedSjekk;
    }

    String navnQuery() {
        return NAVN_QUERY;
    }

    String barnQuery() {
        return BARN_QUERY;
    }

    String søkerQuery() {
        return SØKER_QUERY;
    }

    String annenQuery() {
        return ANNEN_PART_QUERY;
    }

    public int getBarnFødtInnen() {
        return barnFoedtInnen;
    }

    public int getDødSjekk() {
        return doedSjekk;
    }

    public URI getKontonummerURI() {
        return kontonummerURI;
    }

    public URI getMaalformURI() {
        return maalformURI;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingPath=" + getPingPath() + ", enabled=" + isEnabled() + ", url=" + getBaseUri()
                + "]";
    }

    public String identQuery() {
        return IDENT_QUERY;
    }

}
