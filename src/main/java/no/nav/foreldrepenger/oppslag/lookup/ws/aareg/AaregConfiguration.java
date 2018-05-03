package no.nav.foreldrepenger.oppslag.lookup.ws.aareg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import no.nav.foreldrepenger.oppslag.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AaregConfiguration extends WsClient<ArbeidsforholdV3>{

    @SuppressWarnings("unchecked")
    @Bean
    public ArbeidsforholdV3 arbeidsforholdV3(
            @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, ArbeidsforholdV3.class);
    }

    @Bean
    public AaregClient aaregClientWs(ArbeidsforholdV3 arbeidsforholdV3) {
        return new AaregClientWs(arbeidsforholdV3);
    }
}
