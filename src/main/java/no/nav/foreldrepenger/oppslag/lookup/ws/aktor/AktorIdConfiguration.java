package no.nav.foreldrepenger.oppslag.lookup.ws.aktor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;

@Configuration
public class AktorIdConfiguration extends WsClient<AktoerV2> {

    @Bean
    @Qualifier("aktoerV2")
    public AktoerV2 aktoerV2(@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, AktoerV2.class);
    }

    @Bean
    @Qualifier("healthIndicatorAktør")
    public AktoerV2 healthIndicatorAktør(@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
        return createPortForHealthIndicator(serviceUrl, AktoerV2.class);
    }

    @Bean
    public AktorIdClient aktorIdClientWs(@Qualifier("aktoerV2") AktoerV2 aktoerV2,
            @Qualifier("healthIndicatorAktør") AktoerV2 healthIndicator) {
        return new AktorIdClientWs(aktoerV2, healthIndicator);
    }
}
