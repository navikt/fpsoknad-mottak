package no.nav.foreldrepenger.oppslag.arena;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.binding.YtelseskontraktV3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.oppslag" })
public class ArenaConfiguration {

   @Autowired
   public ArenaConfiguration(Environment environment) {
      System.setProperty("SECURITYTOKENSERVICE_URL", environment.getProperty("SECURITYTOKENSERVICE_URL"));
      System.setProperty("FPSELVBETJENING_USERNAME", environment.getProperty("FPSELVBETJENING_USERNAME"));
      System.setProperty("FPSELVBETJENING_PASSWORD", environment.getProperty("FPSELVBETJENING_PASSWORD"));
   }

   @SuppressWarnings("unchecked")
   @Bean
   public YtelseskontraktV3 YtelseskontraktV3(
      @Value("${VIRKSOMHET_YTELSESKONTRAKT_V3_ENDPOINTURL}") String serviceUrl) {
      return new WsClient<YtelseskontraktV3>().createPort(serviceUrl, YtelseskontraktV3.class);
   }
}
