package no.nav.foreldrepenger.mottak.http;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.Constants.TOKENX;
import static org.springframework.retry.RetryContext.NAME;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.web.client.RestOperations;

import com.google.common.base.Splitter;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnVTP;
import no.nav.foreldrepenger.mottak.http.interceptors.TokenExchangeClientRequestInterceptor;
import no.nav.foreldrepenger.mottak.http.interceptors.TokenXConfigFinder;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.spring.validation.interceptor.BearerTokenClientHttpRequestInterceptor;

@Configuration
public class RestClientConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(RestClientConfiguration.class);

    @Bean
    @Primary
    public RestOperations nonTokenXTemplate(RestTemplateBuilder builder, ClientHttpRequestInterceptor... interceptors) {
        var filtered = Arrays.stream(interceptors)
                .filter(not(i -> i.getClass().equals(TokenExchangeClientRequestInterceptor.class)))
                .collect(toList());
        LOG.info("Filtered message interceptorer for non token X er {}", filtered);
        return builder
                .requestFactory(NonRedirectingRequestFactory.class)
                .interceptors(filtered)
                .build();
    }

    @ConditionalOnVTP
    @Bean
    public ClientHttpRequestInterceptor localPassthrough(TokenValidationContextHolder holder) {
        return new BearerTokenClientHttpRequestInterceptor(holder);
    }

    @Bean
    @Qualifier(TOKENX)
    public RestOperations tokenXTemplate(RestTemplateBuilder builder, ClientHttpRequestInterceptor... interceptors) {
        var filtered = Arrays.stream(interceptors)
                .filter(not(i -> i.getClass().equals(BearerTokenClientHttpRequestInterceptor.class)))
                .collect(toList());
        LOG.trace("Filtered message interceptorer for token X er {}",
                filtered.stream()
                        .map(m -> m.getClass().getSimpleName())
                        .collect(toList()));
        return builder
                .requestFactory(NonRedirectingRequestFactory.class)
                .interceptors(filtered)
                .build();
    }

    @Bean
    public TokenXConfigFinder configFinder() {
        return (configs, req) -> {
            LOG.info("Oppslag token X konfig for {}", req.getHost());
            var config = configs.getRegistration().get(Splitter.on(".").splitToList(req.getHost()).get(0));
            if (config != null) {
                LOG.info("Oppslag token X konfig for {} OK", req.getHost());
            } else {
                LOG.info("Oppslag token X konfig for {} fant ingenting", req.getHost());
            }
            return config;
        };
    }

    @Bean
    public List<RetryListener> retryListeners() {
        Logger log = LoggerFactory.getLogger(getClass());

        return List.of(new RetryListener() {

            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
                    Throwable throwable) {
                log.info("Metode {} kastet exception {} for {}. gang",
                        context.getAttribute(NAME), throwable, context.getRetryCount());
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext ctx, RetryCallback<T, E> callback,
                    Throwable t) {
                if (t != null) {
                    log.warn("Metode {} avslutter ikke-vellykket retry etter {}. forsøk ({})",
                            ctx.getAttribute(NAME), ctx.getRetryCount(), t.getMessage(), t);
                } else {
                    if (ctx.getRetryCount() > 0) {
                        log.info("Metode {} avslutter vellykket retry etter {}. forsøk",
                                ctx.getAttribute(NAME), ctx.getRetryCount());
                    }
                }
            }

            @Override
            public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
                var labelField = findField(callback.getClass(), "val$label");
                makeAccessible(labelField);
                var metode = String.class.cast(getField(labelField, callback));
                if (context.getRetryCount() > 0) {
                    log.info("Metode {} gjør retry for {}. gang", metode, context.getRetryCount());
                }
                return true;
            }
        });
    }

}
