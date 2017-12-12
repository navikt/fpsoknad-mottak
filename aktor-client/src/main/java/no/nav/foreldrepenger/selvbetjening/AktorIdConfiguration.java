package no.nav.foreldrepenger.selvbetjening;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import no.nav.foreldrepenger.selvbetjening.cxfclient.CXFClient;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;

@SpringBootConfiguration
@ComponentScan(basePackages= {"no.nav.foreldrepenger.selvbetjening"})
public class AktorIdConfiguration {

   @Bean
   public AktoerV2 aktorV2 (@Value("${AKTOER_V2_ENDPOINTURL}") String aktorUrl,
                            @Value("${SECURITYTOKENSERVICE_URL}") String stsUrl,
                            @Value("${SRVENGANGSSTONAD_USERNAME}") String systemUserName,
                            @Value("${SRVENGANGSSTONAD_PASSWORD}") String systemUserPassword) {

      System.setProperty("no.nav.modig.security.sts.url", stsUrl);
      System.setProperty("no.nav.modig.security.systemuser.username", systemUserName);
      System.setProperty("no.nav.modig.security.systemuser.password", systemUserPassword);

      return new CXFClient<>(AktoerV2.class)
         .configureStsForSystemUser()
         .address(aktorUrl)
         .build();
   }
}

