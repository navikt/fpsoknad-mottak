package no.nav.foreldrepenger.lookup.ws.ytelser.sakogbehandling;

import no.nav.foreldrepenger.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SakOgBehandlingConfiguration extends WsClient<SakOgBehandlingV1> {

    @Bean
    @Qualifier("SakOgBehandlingV1")
    public SakOgBehandlingV1 sakOgBehandlingV1(@Value("${SAKOGBEHANDLING_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, SakOgBehandlingV1.class);
    }

    @Bean
    @Qualifier("healthIndicatorSakOgBehandling")
    public SakOgBehandlingV1 healthIndicatorsakOgBehandling(
            @Value("${SAKOGBEHANDLING_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, SakOgBehandlingV1.class);
    }

    @Bean
    public SakOgBehandlingClient sakOgBehandlingClient(@Qualifier("SakOgBehandlingV1") SakOgBehandlingV1 sakOgBehandlingV1,
            @Qualifier("healthIndicatorSakOgBehandling") SakOgBehandlingV1 healthIdicator) {
        return new SakOgBehandlingClientWs(sakOgBehandlingV1, healthIdicator);
    }

}
