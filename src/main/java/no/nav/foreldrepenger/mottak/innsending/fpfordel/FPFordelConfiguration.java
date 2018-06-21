package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FPFordelConfiguration {

    @Bean
    public RestTemplate restTemplate(FPFordelConfig cfg, ClientHttpRequestInterceptor... interceptors) {

        RestTemplate template = new RestTemplateBuilder()
                .rootUri(cfg.getUri())
                .interceptors(interceptors)
                .errorHandler(new FPFordeResponseErrorHandler())
                .build();
        template.setRequestFactory(requestFactory());
        template.getMessageConverters().add(new MultipartMixedAwareMessageConverter());
        return template;
    }

    private static HttpComponentsClientHttpRequestFactory requestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        final HttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new DefaultRedirectStrategy() {
                    @Override
                    protected boolean isRedirectable(String method) {
                        return false;
                    }
                })
                .build();
        factory.setHttpClient(httpClient);
        return factory;
    }
}
