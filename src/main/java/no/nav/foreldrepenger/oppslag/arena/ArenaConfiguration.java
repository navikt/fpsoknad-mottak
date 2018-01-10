package no.nav.foreldrepenger.oppslag.arena;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import no.nav.arbeid.cxfclient.CXFClient;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.binding.YtelseskontraktV3;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.oppslag" })
public class ArenaConfiguration {

	@Bean
	public YtelseskontraktV3 YtelseskontraktV3(@Value("${VIRKSOMHET:YTELSESKONTRKT_V3_ENDPOINTURL}") String serviceUrl) {
		return new CXFClient<>(YtelseskontraktV3.class)
         .configureStsForSystemUser()
         .address(serviceUrl)
         .build();
	}
}
