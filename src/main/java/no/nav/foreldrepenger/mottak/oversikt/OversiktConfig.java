package no.nav.foreldrepenger.mottak.oversikt;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.util.URIUtil;

@ConfigurationProperties(prefix = "fpoversikt")
public class OversiktConfig {

    private static final String DEFAULT_URI = "https://fpoversikt.intern.nav.no";

    private static final String CONTEXT_PATH = "api";
    private static final String MINE_ARBEIDSFORHOLD_PATH = CONTEXT_PATH + "/arbeid/mineArbeidsforhold";
    private static final String PERSON_OPPSLAG_PATH = CONTEXT_PATH + "/person/info";
    private static final String ANNENPART_AKTØRID_PATH = CONTEXT_PATH + "/annenPart/aktorid";

    private final URI baseUri;

    @ConstructorBinding
    public OversiktConfig(@DefaultValue(DEFAULT_URI) URI baseUri) {
        this.baseUri = baseUri;
    }

    URI mineArbeidsforholdURI() {
        return URIUtil.uri(getBaseUri(), MINE_ARBEIDSFORHOLD_PATH);
    }

    URI personOppslagURI(Ytelse ytelse) {
        return URIUtil.uri(getBaseUri(), PERSON_OPPSLAG_PATH, URIUtil.queryParams("ytelse", ytelse.name()));
    }

    URI aktørid() {
        return URIUtil.uri(getBaseUri(), ANNENPART_AKTØRID_PATH);
    }

    public URI getBaseUri() {
        return baseUri;
    }
}
