package no.nav.foreldrepenger.oppslag.aktor;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AktorIdConfiguration {

   @Autowired
   public AktorIdConfiguration(Environment environment) {
      System.setProperty("SECURITYTOKENSERVICE_URL", environment.getProperty("SECURITYTOKENSERVICE_URL"));
      System.setProperty("FPSELVBETJENING_USERNAME", environment.getProperty("FPSELVBETJENING_USERNAME"));
      System.setProperty("FPSELVBETJENING_PASSWORD", environment.getProperty("FPSELVBETJENING_PASSWORD"));
   }

   @SuppressWarnings("unchecked")
   @Bean
   public AktoerV2 aktorV2(@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
      return new WsClient<AktoerV2>().createPort(serviceUrl, AktoerV2.class);
   }
}
