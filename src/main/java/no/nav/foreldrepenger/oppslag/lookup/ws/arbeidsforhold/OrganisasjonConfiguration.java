package no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold;

import no.nav.foreldrepenger.oppslag.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.organisasjon.v5.binding.OrganisasjonV5;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganisasjonConfiguration extends WsClient<OrganisasjonV5> {

    @Bean
    @Qualifier("organisasjonV5")
    public OrganisasjonV5 organisasjonV5(
            @Value("${VIRKSOMHET_ORGANISASJON_V5_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, OrganisasjonV5.class);
    }

    @Bean
    @Qualifier("healthIndicatorOrganisasjon")
    public OrganisasjonV5 healthIndicatorOrganissjon(
            @Value("${VIRKSOMHET_ORGANISASJON_V5_ENDPOINTURL}") String serviceUrl) {
        return createPortForHealthIndicator(serviceUrl, OrganisasjonV5.class);
    }

    @Bean
    public OrganisasjonClient organisasjonClientWs(@Qualifier("organisasjonV5") OrganisasjonV5 organisasjonV5,
                                              @Qualifier("healthIndicatorOrganisasjon") OrganisasjonV5 healthIndicator) {
        return new OrganisasjonClientWs(organisasjonV5, healthIndicator);
    }
}
