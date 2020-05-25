package no.nav.foreldrepenger.mottak.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfiguration {
    @Bean
    public WebClient webClient(ExchangeFilterFunction... filters) {
        var builder = WebClient.builder();
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }

    @Bean
    public ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            clientRequest
                    .headers()
                    .forEach((name, values) -> System.out.println(name + "->" + values));
            return Mono.just(clientRequest);
        });
    }
}
