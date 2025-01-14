package no.nav.foreldrepenger.mottak.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.httpClientProxyEnabled;

@Configuration(proxyBeanMethods = false)
public class TokenSupportConfiguration {

    @Bean
    public RestClient.Builder tokensupportRestClientBuilder() {
        return RestClient.builder().requestFactory(new JdkClientHttpRequestFactory(httpClientProxyEnabled()));
    }
}
