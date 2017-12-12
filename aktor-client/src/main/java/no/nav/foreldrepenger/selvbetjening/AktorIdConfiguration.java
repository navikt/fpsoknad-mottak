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
public AktoerV2 aktorV2 (@Value("${AKTOER_V2_ENDPOINTURL}") String url) {
         return new CXFClient<>(AktoerV2.class)
             .configureStsForSystemUser()
             .address(url)
             .build();
           }
}

