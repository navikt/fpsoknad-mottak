package no.nav.foreldrepenger.mottak.http;

import static org.springframework.retry.RetryContext.NAME;

import java.util.Arrays;
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

@Configuration
public class RestClientConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(RestClientConfiguration.class);

    @Bean
    @Primary
    public RestOperations restTemplate(RestTemplateBuilder builder, ClientHttpRequestInterceptor... interceptors) {
        LOG.info("Message interceptor er er {}", Arrays.toString(interceptors));
        return builder
                .requestFactory(NonRedirectingRequestFactory.class)
                .interceptors(interceptors)
                .build();
    }

    @Bean
    public List<RetryListener> retryListeners() {
        Logger log = LoggerFactory.getLogger(getClass());

        return List.of(new RetryListener() {

            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
                    Throwable throwable) {
                log.info("Metode {} kastet exception {} for {}. gang",
                        context.getAttribute(NAME), throwable.toString(), context.getRetryCount());
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext ctx, RetryCallback<T, E> callback,
                    Throwable t) {
                if (t != null) {
                    log.warn("Metode {} avslutter ikke-vellykket retry etter {}. forsøk",
                            ctx.getAttribute(NAME), ctx.getRetryCount(), t);
                } else {
                    if (ctx.getRetryCount() > 0) {
                        log.info("Metode {} avslutter vellykket retry etter {}. forsøk",
                                ctx.getAttribute(NAME), ctx.getRetryCount());
                    }
                }
            }

            @Override
            public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
                var labelField = ReflectionUtils.findField(callback.getClass(), "val$label");
                ReflectionUtils.makeAccessible(labelField);
                String metode = (String) ReflectionUtils.getField(labelField, callback);
                if (context.getRetryCount() > 0) {
                    log.info("Metode {} gjør retry for {}. gang", metode, context.getRetryCount());
                }
                return true;
            }
        });
    }

}
