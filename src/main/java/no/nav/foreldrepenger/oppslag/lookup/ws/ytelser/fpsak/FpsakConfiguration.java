package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.fpsak;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.binding.ForeldrepengesakV1;

@Configuration
public class FpsakConfiguration extends WsClient<ForeldrepengesakV1> {

    @Bean
    @Qualifier("fpsakV1")
    public ForeldrepengesakV1 fpsakV1(@Value("${VIRKSOMHET_FORELDREPENGESAK_V1_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, ForeldrepengesakV1.class);
    }

    @Bean
    @Qualifier("healthIndicatorFpsak")
    public ForeldrepengesakV1 healthIndicatorFpsak(
            @Value("${VIRKSOMHET_FORELDREPENGESAK_V1_ENDPOINTURL}") String serviceUrl) {
        return createPortForHealthIndicator(serviceUrl, ForeldrepengesakV1.class);
    }

    @Bean
    public FpsakClient fpsakClientWs(@Qualifier("fpsakV1") ForeldrepengesakV1 fpsakV1,
            @Qualifier("healthIndicatorFpsak") ForeldrepengesakV1 healthIndicator) {
        return new FpsakClientWs(fpsakV1, healthIndicator);
    }
}
