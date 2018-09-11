package no.nav.foreldrepenger.lookup.ws.ytelser.gsak;

import no.nav.foreldrepenger.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.sak.v2.SakV2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsakConfiguration extends WsClient<SakV2> {

    @Bean
    @Qualifier("SakV2")
    public SakV2 sakV2(@Value("${VIRKSOMHET_SAK_V2_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, SakV2.class);
    }

    @Bean
    @Qualifier("healthIndicatorGsak")
    public SakV2 healthIndicatorGsak(
            @Value("${VIRKSOMHET_SAK_V2_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, SakV2.class);
    }

    @Bean
    public GsakClient gsakClientWs(@Qualifier("SakV2") SakV2 sakV2,
            @Qualifier("healthIndicatorGsak") SakV2 healthIdicator) {
        return new GsakClientWs(sakV2, healthIdicator);
    }

}
