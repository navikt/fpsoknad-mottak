package no.nav.foreldrepenger.mottak.http;

import static java.util.function.Predicate.not;
import static org.springframework.retry.RetryContext.NAME;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.web.client.RestOperations;

import com.google.common.base.Splitter;

import no.nav.foreldrepenger.mottak.http.interceptors.TokenXConfigFinder;
import no.nav.security.token.support.spring.validation.interceptor.BearerTokenClientHttpRequestInterceptor;

@Configuration
public class RestClientConfiguration {
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Logger LOG = LoggerFactory.getLogger(RestClientConfiguration.class);

    @Bean
    public RestOperations tokenXTemplate(RestTemplateBuilder b, ClientHttpRequestInterceptor... interceptors) {
        return b.requestFactory(NonRedirectingRequestFactory.class)
                .interceptors(interceptorsWithoutBearerToken(interceptors))
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setReadTimeout(READ_TIMEOUT)
                .build();
    }

    private static List<ClientHttpRequestInterceptor> interceptorsWithoutBearerToken(ClientHttpRequestInterceptor... interceptors) {
        var filtered = Arrays.stream(interceptors)
                .filter(not(i -> i.getClass().equals(BearerTokenClientHttpRequestInterceptor.class)))
                .toList();
        LOG.trace("Filtered message interceptors er {}", filtered);
        return filtered;
    }

    @Bean
    public TokenXConfigFinder configFinder() {
        return (cfgs, req) -> {
            LOG.trace("Oppslag token X konfig for {}", req.getHost());
            var cfg = cfgs.getRegistration().get(Splitter.on(".").splitToList(req.getHost()).get(0));
            if (cfg != null) {
                LOG.trace("Oppslag token X konfig for {} OK", req.getHost());
            } else {
                LOG.trace("Oppslag token X konfig for {} fant ingenting", req.getHost());
            }
            return cfg;
        };
    }

    @Bean
    public List<RetryListener> retryListeners() {
        return List.of(new RetryListener() {

            @Override
            public <T, E extends Throwable> void onError(RetryContext ctx, RetryCallback<T, E> callback, Throwable t) {
                LOG.info("Metode {} kastet exception {} for {}. gang", ctx.getAttribute(NAME), t, ctx.getRetryCount());
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext ctx, RetryCallback<T, E> callback, Throwable t) {
                if (t != null) {
                    LOG.warn("Metode {} avslutter ikke-vellykket retry etter {}. forsøk ({})",
                            ctx.getAttribute(NAME), ctx.getRetryCount(), t.getMessage(), t);
                } else {
                    if (ctx.getRetryCount() > 0) {
                        LOG.info("Metode {} avslutter vellykket retry etter {}. forsøk",
                                ctx.getAttribute(NAME), ctx.getRetryCount());
                    }
                }
            }

            @Override
            public <T, E extends Throwable> boolean open(RetryContext ctx, RetryCallback<T, E> callback) {
                var labelField = findField(callback.getClass(), "val$label");
                makeAccessible(labelField);
                var m = String.class.cast(getField(labelField, callback));
                if (ctx.getRetryCount() > 0) {
                    LOG.info("Metode {} gjør retry for {}. gang", m, ctx.getRetryCount());
                }
                return true;
            }
        });
    }

}
