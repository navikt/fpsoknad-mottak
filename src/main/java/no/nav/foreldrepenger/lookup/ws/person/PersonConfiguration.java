package no.nav.foreldrepenger.lookup.ws.person;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.lookup.TokenHandler;
import no.nav.foreldrepenger.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

@Configuration
public class PersonConfiguration extends WsClient<PersonV3> {

    @Bean
    @Qualifier("personV3")
    public PersonV3 personV3(@Value("${VIRKSOMHET_PERSON_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, PersonV3.class);
    }

    @Bean
    @Qualifier("healthIndicatorPerson")
    public PersonV3 healthIndicatorPerson(@Value("${VIRKSOMHET_PERSON_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, PersonV3.class);
    }

    @Bean
    public Barnutvelger barnutvelger(PersonV3 personV3,
            @Value("${foreldrepenger.selvbetjening.maxmonthsback:24}") int months) {
        return new BarnMorRelasjonSjekkendeBarnutvelger(months);
    }

    @Bean
    public PersonClient personKlientTpsWs(@Qualifier("personV3") PersonV3 personV3,
            @Qualifier("healthIndicatorPerson") PersonV3 healthIndicator, TokenHandler handler,
            Barnutvelger barnutvelger) {
        return new PersonClientTpsWs(personV3, healthIndicator, handler, barnutvelger);
    }

}
