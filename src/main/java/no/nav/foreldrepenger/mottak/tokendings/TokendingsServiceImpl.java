package no.nav.foreldrepenger.mottak.tokendings;

import static no.nav.foreldrepenger.mottak.tokendings.ClientAssertion.assertion;

import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;

public class TokendingsServiceImpl implements TokendingsService {

    private final TokendingsClient tokendingsClient;
    private final String audience;
    private final String clientId;
    private final RSAKey privateRSAKey;

    public TokendingsServiceImpl(TokendingsClient tokendingsClient, String jwtAudience, String clientId, String privateJwk)
            throws ParseException {
        this.tokendingsClient = tokendingsClient;
        this.audience = jwtAudience;
        this.clientId = clientId;
        this.privateRSAKey = RSAKey.parse(privateJwk);
    }

    @Override
    public String exchangeToken(String token, TargetApp targetApp) throws JOSEException {
        return tokendingsClient.exchange(token, assertion(clientId, audience, privateRSAKey), targetApp).accessToken();
    }
}
