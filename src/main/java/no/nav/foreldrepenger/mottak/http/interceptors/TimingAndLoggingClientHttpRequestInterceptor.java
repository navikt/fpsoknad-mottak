package no.nav.foreldrepenger.mottak.http.interceptors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import no.nav.foreldrepenger.common.util.TokenUtil;

@Component
public class TimingAndLoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(TimingAndLoggingClientHttpRequestInterceptor.class);

    private final TokenUtil tokenUtil;
    private final MeterRegistry registry;

    public TimingAndLoggingClientHttpRequestInterceptor(TokenUtil tokenUtil, MeterRegistry registry) {
        this.tokenUtil = tokenUtil;
        this.registry = registry;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        URI uri = UriComponentsBuilder.fromHttpRequest(request).replaceQuery(null).build().toUri();
        Timer t = Timer.builder("rest.calls")
                .tags("uri", uri.getPath(), "method", request.getMethodValue(), "host", uri.getHost())
                .publishPercentiles(0.5, 0.95) // median and 95th percentile
                .publishPercentileHistogram()
                .serviceLevelObjectives(Duration.ofMillis(100))
                .minimumExpectedValue(Duration.ofMillis(100))
                .maximumExpectedValue(Duration.ofSeconds(1))
                .register(registry);
        LOG.info("{} - {}", request.getMethodValue(), uri);
        StopWatch timer = new StopWatch();
        timer.start();
        var respons = execution.execute(request, body);
        Metrics.counter("url", "endpoint", uri.toString(), "method", request.getMethodValue(), "status",
                String.valueOf(respons.getRawStatusCode()))
                .increment();
        timer.stop();
        t.record(timer.getTime(), MILLISECONDS);
        if (hasError(respons.getStatusCode())) {
            LOG.warn("{} - {} - ({}). Dette tok {}ms. ({})", request.getMethodValue(), request.getURI(),
                    respons.getRawStatusCode(), timer.getTime(MILLISECONDS), tokenUtil.getExpiration());
        } else {
            LOG.info("{} - {} - ({}). Dette tok {}ms", request.getMethodValue(), uri,
                    respons.getStatusCode(), timer.getTime(MILLISECONDS));
        }
        return respons;
    }

    protected boolean hasError(HttpStatus code) {
        return code.series() == CLIENT_ERROR || code.series() == SERVER_ERROR;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [tokenUtil=" + tokenUtil + "]";
    }
}
