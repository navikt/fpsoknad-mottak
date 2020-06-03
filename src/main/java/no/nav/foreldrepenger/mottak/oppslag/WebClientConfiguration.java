package no.nav.foreldrepenger.mottak.oppslag;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

    @Bean
    @Qualifier("STS")
    public WebClient webClient(@Value("${sts.uri}") String uri, @Value("${kafka.username}") String systemUser,
            @Value("${kafka.password}") String systemPassword) {
        return WebClient.builder().baseUrl(uri)
                .defaultHeaders(h -> h.setBasicAuth(systemUser, systemPassword))
                .build();
    }

    @Qualifier("REST")
    public WebClient webClient(ExchangeFilterFunction... filters) {
        var builder = WebClient.builder();
        LOG.info("Legger til {} filtre ({})", filters.length, Arrays.toString(filters));
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }

    @Bean
    ExchangeFilterFunction systemTokenAddingFilterFunction(SystemUserTokenService sts) {
        return (req, nextFilter) -> {
            ClientRequest filteredRequest = ClientRequest.from(req).header("Nav-Consumer-Token",
                    "Bearer " + sts.getUserToken().getToken()).build();
            return nextFilter.exchange(filteredRequest);
        };
    }
}
