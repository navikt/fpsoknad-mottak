package no.nav.foreldrepenger.mottak.http;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
public class TokenSupportConfiguration {

    @Bean
    public RestClient.Builder restClient(HttpClient.Builder builder) {
        var httpClient = builder
            .proxy(ProxySelector.getDefault())
            .connectTimeout(Duration.ofSeconds(15))
            .build();
        return RestClient.builder().requestFactory(new JdkClientHttpRequestFactory(httpClient));
    }
}
