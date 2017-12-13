package no.nav.foreldrepenger.selvbetjening;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import no.nav.foreldrepenger.selvbetjening.cxfclient.CXFClient;
import no.nav.foreldrepenger.selvbetjening.cxfclient.STSConfig;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;

@SpringBootConfiguration
@ComponentScan(basePackages= {"no.nav.foreldrepenger.selvbetjening"})
public class InntektConfiguration {
	

   @Bean
   //TODO wrong URL
   public InntektV3 inntektV3 (STSConfig stsConfig,@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
      return new CXFClient<>(InntektV3.class)
         .configureStsForSystemUser(stsConfig)
         .serviceUrl(serviceUrl)
         .build();
   }
}   


