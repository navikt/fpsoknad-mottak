package no.nav.foreldrepenger.lookup.rest;

import static java.util.Collections.singletonList;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.TokenHandler;
import no.nav.foreldrepenger.lookup.rest.sak.StsClient;

@Component
@Order(HIGHEST_PRECEDENCE)
public class OIDCToSAMLTokenExchangingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger LOG = LoggerFactory
            .getLogger(OIDCToSAMLTokenExchangingClientHttpRequestInterceptor.class);

    private final TokenHandler tokenHandler;
    private final StsClient stsClient;

    public OIDCToSAMLTokenExchangingClientHttpRequestInterceptor(StsClient stsClient, TokenHandler tokenHandler) {
        this.stsClient = stsClient;
        this.tokenHandler = tokenHandler;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "Saml " + encode(stsClient.exchangeForSamlToken(tokenHandler.getToken())));
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(singletonList(APPLICATION_JSON));
        return execution.execute(request, body);
    }

    private static String encode(String samlToken) {
        try {
            return Base64.getEncoder().encodeToString(samlToken.getBytes("utf-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [tokenHandler=" + tokenHandler + ", stsClient=" + stsClient + "]";
    }
}