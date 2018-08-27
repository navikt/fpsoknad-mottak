package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import no.nav.foreldrepenger.lookup.ws.WsClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;

@Configuration
public class AaregConfiguration extends WsClient<ArbeidsforholdV3> {

    @Bean
    @Qualifier("arbeidsforholdV3")
    public ArbeidsforholdV3 arbeidsforholdV3(
            @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, ArbeidsforholdV3.class);
    }

    @Bean
    @Qualifier("healthIndicatorAareg")
    public ArbeidsforholdV3 healthIndicatorAareg(
            @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForHealthIndicator(serviceUrl, ArbeidsforholdV3.class);
    }

    @Bean
    public ArbeidsforholdClient aaregClientWs(@Qualifier("arbeidsforholdV3") ArbeidsforholdV3 arbeidsforholdV3,
            @Qualifier("healthIndicatorAareg") ArbeidsforholdV3 healthIndicator,
            OrganisasjonClient organisasjonClient) {
        return new ArbeidsforholdClientWs(arbeidsforholdV3, healthIndicator, organisasjonClient);
    }
}
