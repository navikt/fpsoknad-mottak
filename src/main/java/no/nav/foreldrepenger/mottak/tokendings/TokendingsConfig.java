package no.nav.foreldrepenger.mottak.tokendings;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "token.x")
public class TokendingsConfig {
    private static final Logger LOG = LoggerFactory.getLogger(TokendingsConfig.class);
    @NestedConfigurationProperty
    private final URL wellKnownUrl;
    @NestedConfigurationProperty
    private final String clientId;
    @NestedConfigurationProperty
    private final String privateJwk;

    @ConstructorBinding
    public TokendingsConfig(URL wellKnownUrl, String clientId, String privateJwk) {
        this.wellKnownUrl = wellKnownUrl;
        this.clientId = clientId;
        this.privateJwk = privateJwk;
        LOG.info("KONSTRUERT " + this);
    }

    public URL getWellKnownUrl() {
        return wellKnownUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getPrivateJwk() {
        return privateJwk;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [wellKnownUrl=" + wellKnownUrl + ", clientId=" + clientId + ", privateJwk=" + privateJwk + "]";
    }
}
