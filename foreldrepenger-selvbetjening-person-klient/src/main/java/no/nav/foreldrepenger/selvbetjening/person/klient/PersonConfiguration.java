package no.nav.foreldrepenger.selvbetjening.person.klient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.arbeid.cxfclient.CXFClient;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

@Configuration
public class PersonConfiguration {

	
	@Bean
	public ChildSelector barnFilter(PersonV3 personV3,@Value("${foreldrepenger.selvbetjening.maxmonthsback:12}") int months) {
		return new ReverseValidatingChildSelector(months);
	}

	@Bean
	public PersonV3 personV3(@Value("${PERSON_V3_ENDPOINTURL}") String serviceUrl) {
		return new CXFClient<>(PersonV3.class).configureStsForSystemUser().address(serviceUrl).build();
	}

}
