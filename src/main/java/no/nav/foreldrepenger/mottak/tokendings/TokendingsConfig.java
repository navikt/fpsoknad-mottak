package no.nav.foreldrepenger.mottak.tokendings;

import static com.nimbusds.jose.jwk.RSAKey.parse;

import java.text.ParseException;

import org.springframework.core.env.Environment;

import com.nimbusds.jose.jwk.RSAKey;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;

@ConditionalOnK8s
public class TokendingsConfig {
    private final String wellKnownUrl;
    private final String clientId;
    private final RSAKey privateRSAKey;

    public TokendingsConfig(Environment env) {
        this.wellKnownUrl = env.getRequiredProperty("token.x.well.known.url");
        this.clientId = env.getRequiredProperty("token.x.client.id");
        try {
            this.privateRSAKey = parse(env.getRequiredProperty("token.x.private.jwk"));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public RSAKey getPrivateRSAKey() {
        return privateRSAKey;
    }

    public String getWellKnownUrl() {
        return wellKnownUrl;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [wellKnownUrl=" + wellKnownUrl + ", clientId=" + clientId + "]";
    }
}
