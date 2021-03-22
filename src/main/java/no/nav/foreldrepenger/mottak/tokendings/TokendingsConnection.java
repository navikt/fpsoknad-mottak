package no.nav.foreldrepenger.mottak.tokendings;

import static com.nimbusds.oauth2.sdk.auth.JWTAuthentication.CLIENT_ASSERTION_TYPE;
import static no.nav.foreldrepenger.mottak.tokendings.TokendingsClientAssertion.clientAssertion;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

//@ConditionalOnK8s
public class TokendingsConnection {

    private final WebClient client;
    private final TokendingsMetadata metadata;
    private final TokendingsConfig cfg;

    public TokendingsConnection(TokendingsConfig cfg) {
        this.client = WebClient.create();
        this.cfg = cfg;
        this.metadata = metadataFra(cfg.getWellKnownUrl());
    }

    public TokendingsResponse exchange(String token, TokendingsTargetApp targetApp) {
        return exchange(token, targetApp.asAudience());
    }

    private TokendingsResponse exchange(String subjectToken, String audience) {
        var form = new LinkedMultiValueMap<>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        form.add("client_assertion_type", CLIENT_ASSERTION_TYPE);
        form.add("client_assertion", clientAssertion(cfg.getClientId(), metadata.tokenEndpoint().toString(), cfg.getPrivateRSAKey()));
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

    private TokendingsMetadata metadataFra(String wellKnownUrl) {
        return client
                .get()
                .uri(wellKnownUrl)
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TokendingsMetadata.class)
                .block();
    }
}
