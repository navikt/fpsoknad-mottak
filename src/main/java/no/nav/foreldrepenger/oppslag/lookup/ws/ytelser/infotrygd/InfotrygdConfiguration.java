package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.infotrygd;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.InfotrygdSakV1;

@Configuration
public class InfotrygdConfiguration extends WsClient<InfotrygdSakV1> {

    @Bean
    @Qualifier("infotrygdSakV1")
    public InfotrygdSakV1 infotrygdSakV1(@Value("${VIRKSOMHET_INFOTRYGDSAK_V1_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, InfotrygdSakV1.class);
    }

    @Bean
    @Qualifier("healthIndicatorInfotrygd")
    public InfotrygdSakV1 healthIndicatorInfotrygd(
            @Value("${VIRKSOMHET_INFOTRYGDSAK_V1_ENDPOINTURL}") String serviceUrl) {
        return createPortForHealthIndicator(serviceUrl, InfotrygdSakV1.class);
    }

    @Bean
    public InfotrygdClient infotrygdClientWs(@Qualifier("infotrygdSakV1") InfotrygdSakV1 infotrygdSakV1,
            @Qualifier("healthIndicatorInfotrygd") InfotrygdSakV1 healthIdicator) {
        return new InfotrygdClientWs(infotrygdSakV1, healthIdicator);
    }

}
