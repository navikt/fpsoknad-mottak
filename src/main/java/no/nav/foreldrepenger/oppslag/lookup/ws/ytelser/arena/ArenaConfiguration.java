package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.arena;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import no.nav.foreldrepenger.oppslag.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.binding.YtelseskontraktV3;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.oppslag" })
public class ArenaConfiguration extends WsClient<YtelseskontraktV3> {

    @Bean
    @Qualifier("YtelseskontraktV3")
    public YtelseskontraktV3 YtelseskontraktV3(
            @Value("${VIRKSOMHET_YTELSESKONTRAKT_V3_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, YtelseskontraktV3.class);
    }

    @Bean
    @Qualifier("healthIndicatorArena")
    public YtelseskontraktV3 healthIndicatorArena(
            @Value("${VIRKSOMHET_YTELSESKONTRAKT_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForHealthIndicator(serviceUrl, InntektV3.class);
    }

    @Bean
    public ArenaClient arenaClientWs(@Qualifier("YtelseskontraktV3") YtelseskontraktV3 ytelser,
            @Qualifier("healthIndicatorArena") YtelseskontraktV3 healthIndicator) {
        return new ArenaClientWs(ytelser, healthIndicator);
    }
}
