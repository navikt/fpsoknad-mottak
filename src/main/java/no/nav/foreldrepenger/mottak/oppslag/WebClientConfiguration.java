package no.nav.foreldrepenger.mottak.oppslag;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

    @Bean
    public WebClient webClient(@Value("${sts.uri}") String uri, @Value("${kafka.username}") String serviceUser,
            @Value("${kafka.password}") String servicePwd, ExchangeFilterFunction... filters) {
        var builder = WebClient.builder().baseUrl(uri)
                .defaultHeaders(header -> header.setBasicAuth(serviceUser, servicePwd));
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }
}
