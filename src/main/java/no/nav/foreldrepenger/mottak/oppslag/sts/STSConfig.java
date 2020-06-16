package no.nav.foreldrepenger.mottak.oppslag.sts;

import static com.nimbusds.oauth2.sdk.GrantType.CLIENT_CREDENTIALS;

import java.net.URI;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.util.UriBuilder;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "sts")
public class STSConfig extends AbstractConfig {

    private static final String GRANT_TYPE = "grant_type";
    private static final String DEFAULT_PATH = "/rest/v1/sts/token";
    private static final String DEFAULT_SLACK = "20s";
    private static final String PING_PATH = "/.well-known/openid-configuration";
    private static final String SCOPE = "scope";
    private final String username;
    private final String password;
    private final Duration slack;
    private final String stsPath;

    @ConstructorBinding
    public STSConfig(String baseUri, @DefaultValue(DEFAULT_SLACK) Duration slack, String username, String password,
            @DefaultValue(PING_PATH) String pingPath, @DefaultValue(DEFAULT_PATH) String stsPath,
            boolean log) {
        super(baseUri, pingPath, log);
        this.stsPath = stsPath;
        this.slack = slack;
        this.username = username;
        this.password = password;
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

    public String getStsPath() {
        return stsPath;
    }

    URI getStsURI(UriBuilder b) {
        return b.path(stsPath)
                .queryParam(GRANT_TYPE, CLIENT_CREDENTIALS.getValue())
                .queryParam(SCOPE, "openid")
                .build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[username=" + username + ", password=" + password + ", slack=" + slack
                + ", stsPath=" + stsPath + "]";
    }

}
