package no.nav.foreldrepenger.oppslag.medl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.medlemskap.v2.MedlemskapV2;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.oppslag" })
public class MedlConfiguration {

    @SuppressWarnings("unchecked")
    @Bean
    public MedlemskapV2 medlemskapV2(@Value("${VIRKSOMHET_MEDLEMSKAP_V2_ENDPOINTURL}") String serviceUrl) {
        return new WsClient<MedlemskapV2>().createPort(serviceUrl, MedlemskapV2.class);
    }
}
