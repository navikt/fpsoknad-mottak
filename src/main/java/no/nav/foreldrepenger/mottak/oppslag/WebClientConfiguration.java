package no.nav.foreldrepenger.mottak.oppslag;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);
    private final String serviceUser;

    private final String servicePwd;

    public WebClientConfiguration(@Value("${kafka.username}") String serviceUser,
            @Value("${kafka.password}") String servicePwd) {
        this.serviceUser = serviceUser;
        this.servicePwd = servicePwd;
    }

    @Bean
    public WebClient webClient(ExchangeFilterFunction... filters) {
        var builder = WebClient.builder().defaultHeaders(header -> header.setBasicAuth(serviceUser, servicePwd));
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }

    @Bean
    public ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            clientRequest
                    .headers().forEach((name, values) -> LOG.info("{} -> {}", name, values));
            return Mono.just(clientRequest);
        });
    }

}
