package no.nav.foreldrepenger.mottak.http;

import no.nav.security.token.support.client.core.http.OAuth2HttpClient;
import no.nav.security.token.support.client.spring.oauth2.DefaultOAuth2HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class TokenSupportConfiguration {

    @Bean
    public OAuth2HttpClient tokensupportOauth2HttpClient() {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .proxy(ProxySelector.getDefault())
                .build();

        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        var restclient = RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory(httpClient))
            .build();
        return new DefaultOAuth2HttpClient(restclient);
    }
}
