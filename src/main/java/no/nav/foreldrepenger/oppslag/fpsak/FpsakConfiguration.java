package no.nav.foreldrepenger.oppslag.fpsak;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.binding.ForeldrepengesakV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FpsakConfiguration {

   @Autowired
   public FpsakConfiguration(Environment environment) {
      System.setProperty("SECURITYTOKENSERVICE_URL", environment.getProperty("SECURITYTOKENSERVICE_URL"));
      System.setProperty("FPSELVBETJENING_USERNAME", environment.getProperty("FPSELVBETJENING_USERNAME"));
      System.setProperty("FPSELVBETJENING_PASSWORD", environment.getProperty("FPSELVBETJENING_PASSWORD"));
   }

   @SuppressWarnings("unchecked")
   @Bean
   public ForeldrepengesakV1 fpsakV1(@Value("${VIRKSOMHET_FORELDREPENGESAK_V1_ENDPOINTURL:}") String serviceUrl) {
      return new WsClient<ForeldrepengesakV1>().createPort(serviceUrl, ForeldrepengesakV1.class);
   }
}
