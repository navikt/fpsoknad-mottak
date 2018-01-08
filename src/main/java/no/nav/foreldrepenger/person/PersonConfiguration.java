package no.nav.foreldrepenger.person;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.arbeid.cxfclient.CXFClient;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

@Configuration
public class PersonConfiguration {

	@Bean
	public Barnutvelger barneVelger(PersonV3 personV3,
	        @Value("${foreldrepenger.selvbetjening.maxmonthsback:12}") int months) {
		return new BarnMorRelasjonSjekkendeBarnutvelger(months);
	}

	@Bean
	public PersonV3 personV3(@Value("${VIRKSOMHET_PERSON_V3_ENDPOINTURL}") String serviceUrl) {
		return new CXFClient<>(PersonV3.class).configureStsForSystemUser().address(serviceUrl).build();
	}

	@Bean
	public PersonKlient prsonKlient(Barnutvelger barneVelger, PersonV3 person) {
		return new PersonKlient(person, barneVelger);
	}
}
