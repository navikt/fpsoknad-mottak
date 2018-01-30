package no.nav.foreldrepenger.oppslag.infotrygd;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.InfotrygdSakV1;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.oppslag" })
public class InfotrygdConfiguration {

    @SuppressWarnings("unchecked")
    @Bean
    public InfotrygdSakV1 infotrygdSakV1(@Value("${VIRKSOMHET_INFOTRYGDSAK_V1_ENDPOINTURL}") String serviceUrl) {
        return new WsClient<InfotrygdSakV1>().createPort(serviceUrl, InfotrygdSakV1.class);
    }

}
