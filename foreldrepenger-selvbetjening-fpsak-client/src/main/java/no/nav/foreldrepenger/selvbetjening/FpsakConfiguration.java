package no.nav.foreldrepenger.selvbetjening;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import no.nav.arbeid.cxfclient.CXFClient;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.binding.ForeldrepengesakV1;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.selvbetjening" })
public class FpsakConfiguration {

	@Bean
	public ForeldrepengesakV1 fpsakV1(@Value("${VIRKSOMHET:FORELDREPENGESAK_V1_ENDPOINTURL}") String serviceUrl) {
		return new CXFClient<>(ForeldrepengesakV1.class).configureStsForSystemUser().address(serviceUrl).build();
	}
}
