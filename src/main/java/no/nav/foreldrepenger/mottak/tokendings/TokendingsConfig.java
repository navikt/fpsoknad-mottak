package no.nav.foreldrepenger.mottak.tokendings;

import static com.nimbusds.jose.jwk.RSAKey.parse;

import java.net.URI;
import java.text.ParseException;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.nimbusds.jose.jwk.RSAKey;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;

@ConditionalOnK8s
@ConfigurationProperties(prefix = "token.x")
public class TokendingsConfig {
    @NestedConfigurationProperty
    private final URI wellKnownUrl;
    @NestedConfigurationProperty
    private final String clientId;
    @NestedConfigurationProperty
    private final String privateJwk;

    @ConstructorBinding
    public TokendingsConfig(URI wellKnownUrl, String clientId, String privateJwk) throws ParseException {
        this.wellKnownUrl = wellKnownUrl;
        this.clientId = clientId;
        this.privateJwk = privateJwk;
    }

    public RSAKey getPrivateRSAKey() {
        try {
            return parse(privateJwk);

        } catch (ParseException e) {
            throw new IllegalArgumentException(e);

        }
    }

    public URI getWellKnownUrl() {
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
        return getClass().getSimpleName() + " [wellKnownUrl=" + wellKnownUrl + ", clientId=" + clientId + "]";
    }
}
