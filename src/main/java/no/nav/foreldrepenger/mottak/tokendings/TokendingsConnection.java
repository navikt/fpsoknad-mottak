package no.nav.foreldrepenger.mottak.tokendings;

import static com.nimbusds.oauth2.sdk.auth.JWTAuthentication.CLIENT_ASSERTION_TYPE;
import static no.nav.foreldrepenger.mottak.tokendings.TokendingsClientAssertion.clientAssertionFra;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;

@ConditionalOnK8s
public class TokendingsConnection {
    private static final Logger LOG = LoggerFactory.getLogger(TokendingsConnection.class);

    private final WebClient client;
    private final TokendingsMetadata metadata;
    private final TokendingsConfig cfg;

    public TokendingsConnection(TokendingsConfig cfg) {
        this.client = WebClient.create();
        this.cfg = cfg;
        this.metadata = metadataFra(cfg.getWellKnownUrl());
    }

    public TokendingsResponse exchange(String subjectToken, TokendingsTargetApp targetApp) {
        return exchange(clientAssertion(), subjectToken, targetApp.asAudience());
    }

    private String clientAssertion() {
        return clientAssertionFra(cfg.getClientId(), metadata.tokenEndpoint().toString(), cfg.getPrivateRSAKey());
    }

    private TokendingsResponse exchange(String clientAssertion, String subjectToken, String audience) {
        var form = new LinkedMultiValueMap<>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        form.add("client_assertion_type", CLIENT_ASSERTION_TYPE);
        form.add("client_assertion", clientAssertion);
        form.add("subject_token_type", "urn:ietf:params:oauth:token-type:jwt");
        form.add("subject_token", subjectToken);
        form.add("audience", audience);
        return client
                .post()
                .uri(metadata.tokenEndpoint())
                .contentType(APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(TokendingsResponse.class)
                .block();
    }

    private TokendingsMetadata metadataFra(URI wellKnownUrl) {
        return client
                .get()
                .uri(wellKnownUrl)
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TokendingsMetadata.class)
                .block();
    }

}
