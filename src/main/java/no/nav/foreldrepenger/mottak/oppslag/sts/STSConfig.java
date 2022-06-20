package no.nav.foreldrepenger.mottak.oppslag.sts;

import static com.nimbusds.oauth2.sdk.GrantType.CLIENT_CREDENTIALS;
import static no.nav.foreldrepenger.common.util.StringUtil.mask;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

import java.net.URI;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters.FormInserter;
import org.springframework.web.util.UriBuilder;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "sts")
public class STSConfig extends AbstractConfig {

    private static final String DEFAULT_URI = "https://security-token-service.nais.adeo.no";
    private static final String DEFAULT_BASE_PATH = "/rest/v1/sts";
    private static final String DEFAULT_TOKEN_PATH = "/token";
    private static final String PING_PATH = ".well-known/openid-configuration";

    private static final String DEFAULT_SLACK = "20s";
    private static final String GRANT_TYPE = "grant_type";
    private static final String SCOPE = "scope";

    @Value("${kafka.username}")
    private String username;
    @Value("${kafka.password}")
    private String password;

    private final String tokenPath;
    private final Duration slack;

    @ConstructorBinding
    public STSConfig(@DefaultValue(DEFAULT_URI) URI baseUri,
                     @DefaultValue(DEFAULT_TOKEN_PATH) String tokenPath,
                     @DefaultValue(PING_PATH) String pingPath,
                     @DefaultValue(DEFAULT_SLACK) Duration slack,
                     @DefaultValue("true") boolean enabled) {
        super(uri(baseUri, DEFAULT_BASE_PATH), pingPath, enabled);
        this.tokenPath = tokenPath;
        this.slack = slack;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Duration getSlack() {
        return slack;
    }

    public String getTokenPath() {
        return tokenPath;
    }

    URI getTokenPath(UriBuilder b) {
        return b.path(tokenPath)
                .build();
    }

    FormInserter<String> stsBody() {
        var m = new LinkedMultiValueMap<String, String>();
        m.add(GRANT_TYPE, CLIENT_CREDENTIALS.getValue());
        m.add(SCOPE, "openid");
        return fromFormData(m);
    }

    @Override
    public String toString() {
        return "STSConfig{" +
            "username='" + mask(username) + '\'' +
            ", password='" + mask(password) + '\'' +
            ", tokenPath='" + tokenPath + '\'' +
            ", slack=" + slack +
            "} " + super.toString();
    }
}
