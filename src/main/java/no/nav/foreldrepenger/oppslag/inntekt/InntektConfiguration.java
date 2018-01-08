package no.nav.foreldrepenger.oppslag.inntekt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import no.nav.arbeid.cxfclient.CXFClient;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.selvbetjening" })
public class InntektConfiguration {

	@Bean
	public InntektV3 inntektV3(@Value("${VIRKSOMHET_INNTEKT_V3_ENDPOINTURL}") String serviceUrl) {
		return new CXFClient<>(InntektV3.class).configureStsForSystemUser().address(serviceUrl).build();
	}
}
