package no.nav.foreldrepenger.mottak.config;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter;
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
        template.getMessageConverters().add(new MultipartMixedAwareMessageConverter());
        return template;
    }

    @Bean
    public List<RetryListener> retryListeners() {
        Logger log = LoggerFactory.getLogger(getClass());

        return Collections.singletonList(new RetryListener() {

            @Override
            public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
                Field labelField = ReflectionUtils.findField(callback.getClass(), "val$label");
                ReflectionUtils.makeAccessible(labelField);
                String label = (String) ReflectionUtils.getField(labelField, callback);
                log.trace("Starting retryable method {}", label);
                return true;
            }

            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
                    Throwable throwable) {
                log.warn("Retryable method {} threw {}th exception {}",
                        context.getAttribute("context.name"), context.getRetryCount(), throwable.toString());
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
                    Throwable throwable) {
                log.trace("Finished retryable method {} {}", context.getRetryCount(),
                        context.getAttribute("context.name"));
            }
        });
    }

}
