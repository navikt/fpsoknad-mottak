package no.nav.foreldrepenger.oppslag.infotrygd;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.InfotrygdSakV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.oppslag" })
public class InfotrygdConfiguration {

   @Autowired
   public InfotrygdConfiguration(Environment environment) {
      System.setProperty("SECURITYTOKENSERVICE_URL", environment.getProperty("SECURITYTOKENSERVICE_URL"));
      System.setProperty("FPSELVBETJENING_USERNAME", environment.getProperty("FPSELVBETJENING_USERNAME"));
      System.setProperty("FPSELVBETJENING_PASSWORD", environment.getProperty("FPSELVBETJENING_PASSWORD"));
   }

   @SuppressWarnings("unchecked")
   @Bean
   public InfotrygdSakV1 infotrygdSakV1(@Value("${VIRKSOMHET_INFOTRYGDSAK_V1_ENDPOINTURL}") String serviceUrl) {
      return new WsClient<InfotrygdSakV1>().createPort(serviceUrl, InfotrygdSakV1.class);
   }

}
