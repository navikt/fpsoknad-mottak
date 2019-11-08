package no.nav.foreldrepenger.mottak.config;

import static java.util.Collections.singletonList;
import static org.springframework.retry.RetryContext.NAME;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.http.NonRedirectingRequestFactory;

@Configuration
public class RestClientConfiguration {
    @Bean
    @Primary
    public RestOperations restTemplate(ClientHttpRequestInterceptor... interceptors) {
        var template = new RestTemplateBuilder()
                .requestFactory(NonRedirectingRequestFactory.class)
                .interceptors(interceptors)
                .build();

        template.getMessageConverters().add(new FormHttpMessageConverter());
        return template;
    }

    @Bean
    public List<RetryListener> retryListeners() {
        Logger log = LoggerFactory.getLogger(getClass());

        return singletonList(new RetryListener() {

            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
                    Throwable throwable) {
                log.warn("Metode {} kastet exception {} for {}. gang",
                        context.getAttribute(NAME), throwable.toString(), context.getRetryCount());
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
                    Throwable t) {
                if (t != null) {
                    log.warn("Metode {} avslutter ikke-vellykket retry etter {}. forsøk",
                            context.getAttribute(NAME), context.getRetryCount());
                } else {
                    if (context.getRetryCount() > 0) {
                        log.info("Metode {} avslutter vellykket retry etter {}. forsøk",
                                context.getAttribute(NAME), context.getRetryCount());
                    } else {
                        log.trace("Metode {} avslutter vellykket uten retry", context.getAttribute(NAME));
                    }
                }
            }

            @Override
            public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
                log.info("Metode {} gjør retry for {}. gang",
                        context.getAttribute(NAME), context.getRetryCount());
                return true;
            }
        });
    }

}
