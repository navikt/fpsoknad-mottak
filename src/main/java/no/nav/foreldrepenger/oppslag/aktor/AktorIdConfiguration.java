package no.nav.foreldrepenger.oppslag.aktor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;

@Configuration
public class AktorIdConfiguration {

	@SuppressWarnings("unchecked")
	@Bean
	public AktoerV2 aktorV2(@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
		return new WsClient<AktoerV2>().createPort(serviceUrl, AktoerV2.class);
	}
}
