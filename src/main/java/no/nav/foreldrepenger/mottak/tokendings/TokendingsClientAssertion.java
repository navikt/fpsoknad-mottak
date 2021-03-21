package no.nav.foreldrepenger.mottak.tokendings;

import static com.nimbusds.jose.JOSEObjectType.JWT;
import static com.nimbusds.jose.JWSAlgorithm.RS256;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader.Builder;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

class TokendingsClientAssertion {
    private TokendingsClientAssertion() {

    }

    static String clientAssertionFra(String clientId, String audience, RSAKey rsaKey) {
        var now = Date.from(Instant.now());
        return sign(new JWTClaimsSet.Builder()
                .subject(clientId)
                .issuer(clientId)
                .audience(audience)
                .issueTime(now)
                .notBeforeTime(now)
                .expirationTime(Date.from(Instant.now().plusSeconds(60)))
                .jwtID(UUID.randomUUID().toString())
                .build(), rsaKey)
                        .serialize();

    }

    private static SignedJWT sign(JWTClaimsSet claimsSet, RSAKey rsaKey) {
        var signedJWT = new SignedJWT(new Builder(RS256)
                .keyID(rsaKey.getKeyID())
                .type(JWT).build(), claimsSet);
        try {
            signedJWT.sign(new RSASSASigner(rsaKey.toPrivateKey()));
            return signedJWT;
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }
    }
}
