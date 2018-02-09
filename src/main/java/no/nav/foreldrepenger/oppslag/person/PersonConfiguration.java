package no.nav.foreldrepenger.oppslag.person;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class PersonConfiguration {

   @Autowired
   public PersonConfiguration(Environment environment) {
      System.setProperty("SECURITYTOKENSERVICE_URL", environment.getProperty("SECURITYTOKENSERVICE_URL"));
      System.setProperty("FPSELVBETJENING_USERNAME", environment.getProperty("FPSELVBETJENING_USERNAME"));
      System.setProperty("FPSELVBETJENING_PASSWORD", environment.getProperty("FPSELVBETJENING_PASSWORD"));
   }

   @Bean
   public Barnutvelger barneVelger(PersonV3 personV3,
                                   @Value("${foreldrepenger.selvbetjening.maxmonthsback:12}") int months) {
      return new BarnMorRelasjonSjekkendeBarnutvelger(months);
   }

   @SuppressWarnings("unchecked")
   @Bean
   public PersonV3 personV3(@Value("${VIRKSOMHET_PERSON_V3_ENDPOINTURL}") String serviceUrl) {
      return new WsClient<PersonV3>().createPort(serviceUrl, PersonV3.class);
   }

   @Bean
   public PersonClient prsonKlient(Barnutvelger barneVelger, PersonV3 person) {
      return new PersonClient(person, barneVelger);
   }
}
