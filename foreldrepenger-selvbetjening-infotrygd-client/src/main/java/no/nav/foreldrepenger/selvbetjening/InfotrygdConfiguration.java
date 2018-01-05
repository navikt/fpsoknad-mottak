package no.nav.foreldrepenger.selvbetjening;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import no.nav.arbeid.cxfclient.CXFClient;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.InfotrygdSakV1;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.selvbetjening" })
public class InfotrygdConfiguration {

	@Bean
	public InfotrygdSakV1 infotrygdSakV1(@Value("${VIRKSOMHET:INFOTRYGDSAK_V1_ENDPOINTURL}") String serviceUrl) {
		return new CXFClient<>(InfotrygdSakV1.class).configureStsForSystemUser().address(serviceUrl).build();
	}

}
