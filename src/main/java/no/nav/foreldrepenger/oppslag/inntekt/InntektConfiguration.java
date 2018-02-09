package no.nav.foreldrepenger.oppslag.inntekt;

import no.nav.foreldrepenger.oppslag.ws.CallIdHeader;
import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.oppslag" })
public class InntektConfiguration {

   @Autowired
   public InntektConfiguration(Environment environment) {
      System.setProperty("SECURITYTOKENSERVICE_URL", environment.getProperty("SECURITYTOKENSERVICE_URL"));
      System.setProperty("FPSELVBETJENING_USERNAME", environment.getProperty("FPSELVBETJENING_USERNAME"));
      System.setProperty("FPSELVBETJENING_PASSWORD", environment.getProperty("FPSELVBETJENING_PASSWORD"));
   }

   @SuppressWarnings("unchecked")
   @Bean
   public InntektV3 inntektV3(@Value("${VIRKSOMHET_INNTEKT_V3_ENDPOINTURL}") String serviceUrl) {
      return new WsClient<InntektV3>().createPort(serviceUrl, InntektV3.class, new CallIdHeader());
   }
}
