package no.nav.foreldrepenger.mottak.tokendings;

import static com.nimbusds.oauth2.sdk.auth.JWTAuthentication.CLIENT_ASSERTION_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

public class TokendingsClient {

    private final WebClient client;

    public TokendingsClient(WebClient client) {
        this.client = client;
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
