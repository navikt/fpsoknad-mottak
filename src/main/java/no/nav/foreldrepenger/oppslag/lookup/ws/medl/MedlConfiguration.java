package no.nav.foreldrepenger.oppslag.lookup.ws.medl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.medlemskap.v2.MedlemskapV2;

@Configuration
public class MedlConfiguration extends WsClient<MedlemskapV2> {

    @Bean
    @Qualifier("medlemskapV2")
    public MedlemskapV2 medlemskapV2(@Value("${VIRKSOMHET_MEDLEMSKAP_V2_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, MedlemskapV2.class);
    }

    @Bean
    @Qualifier("healthIndicator")
    public MedlemskapV2 healthIndicator(@Value("${VIRKSOMHET_MEDLEMSKAP_V2_ENDPOINTURL}") String serviceUrl) {
        return createPortForHealthIndicator(serviceUrl, MedlemskapV2.class);
    }

    @Bean
    public MedlClient medlClientWs(MedlemskapV2 medlemskapV2, MedlemskapV2 healthIndicator) {
        return new MedlClientWs(medlemskapV2, healthIndicator);
    }
}
