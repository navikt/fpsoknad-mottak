package no.nav.foreldrepenger.oppslag.medl;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.medlemskap.v2.MedlemskapV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.oppslag" })
public class MedlConfiguration {

   @Autowired
   public MedlConfiguration(Environment environment) {
      System.setProperty("SECURITYTOKENSERVICE_URL", environment.getProperty("SECURITYTOKENSERVICE_URL"));
      System.setProperty("FPSELVBETJENING_USERNAME", environment.getProperty("FPSELVBETJENING_USERNAME"));
      System.setProperty("FPSELVBETJENING_PASSWORD", environment.getProperty("FPSELVBETJENING_PASSWORD"));
   }

   @SuppressWarnings("unchecked")
   @Bean
   public MedlemskapV2 medlemskapV2(@Value("${VIRKSOMHET_MEDLEMSKAP_V2_ENDPOINTURL}") String serviceUrl) {
      return new WsClient<MedlemskapV2>().createPort(serviceUrl, MedlemskapV2.class);
   }
}
