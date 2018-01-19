package no.nav.foreldrepenger.oppslag.person;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

@Configuration
public class PersonConfiguration {

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
