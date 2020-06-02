package no.nav.foreldrepenger.mottak.oppslag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebClientConfiguration {

    private final String serviceUser;

    private final String servicePwd;
    /*
     * 
     * @Bean public WebClient webClient(ExchangeFilterFunction... filters) { var
     * builder = WebClient.builder().defaultHeaders(header ->
     * header.setBasicAuth(serviceUser, servicePwd));
     * Arrays.stream(filters).forEach(builder::filter); return builder.build(); }
     * 
     * @Bean public ExchangeFilterFunction logRequest() { return
     * ExchangeFilterFunction.ofRequestProcessor(clientRequest -> { clientRequest
     * .headers() .forEach((name, values) -> System.out.println(name + "->" +
     * values)); return Mono.just(clientRequest); }); }
     */

    public WebClientConfiguration(@Value("${kafka.username}") String serviceUser,
            @Value("${kafka.password}") String servicePwd) {
        this.serviceUser = serviceUser;
        this.servicePwd = servicePwd;
    }
}
