package no.nav.foreldrepenger.mottak.tokendings;

import static com.nimbusds.oauth2.sdk.auth.JWTAuthentication.CLIENT_ASSERTION_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;

@ConditionalOnK8s
public class TokendingsClient {

    private final WebClient client;
    private final TokendingsConfig cfg;
    private final TokendingsConfigurationMetadata metadata;

    public TokendingsClient(TokendingsConfig cfg) {
        this.client = WebClient.create();
        this.cfg = cfg;
        this.metadata = getMetadata(cfg.getWellKnownUrl());
    }

    private TokendingsConfigurationMetadata getMetadata(URI wellKnownUrl) {
        return client
                .get()
                .uri(wellKnownUrl)
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TokendingsConfigurationMetadata.class)
                .block();

    }

    public TokendingsResponse exchange(String clientAssertion, String subjectToken, String audience) {
        var form = new LinkedMultiValueMap<>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        form.add("client_assertion_type", CLIENT_ASSERTION_TYPE);
        form.add("client_assertion", clientAssertion);
        form.add("subject_token_type", "urn:ietf:params:oauth:token-type:jwt");
        form.add("subject_token", subjectToken);
        form.add("audience", audience);

        return client
                .post()
                // .uri("URL")
                .contentType(APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(TokendingsResponse.class)
                .block();
    }

}
