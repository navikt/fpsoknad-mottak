package no.nav.foreldrepenger.aktor;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

import no.nav.arbeid.cxfclient.CXFClient;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;

@Configuration
public class AktorIdConfiguration {

	@Bean
	public AktoerV2 aktorV2(@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
		return new CXFClient<>(AktoerV2.class).configureStsForSystemUser().address(serviceUrl).build();
	}
}
