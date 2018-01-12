package no.nav.foreldrepenger.oppslag.fpsak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.arbeid.cxfclient.CXFClient;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.binding.ForeldrepengesakV1;

@Configuration
public class FpsakConfiguration {

	@Bean
	public ForeldrepengesakV1 fpsakV1(@Value("${VIRKSOMHET_FORELDREPENGESAK_V1_ENDPOINTURL:}") String serviceUrl) {
		return new CXFClient<>(ForeldrepengesakV1.class)
         .configureStsForSystemUser()
         .address(serviceUrl)
         .build();
	}
}
