package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;
import no.nav.foreldrepenger.mottak.util.URIUtil;

@Component
public class OrganisasjonConnection implements PingEndpointAware {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonConnection.class);
    private final OrganisasjonConfig cfg;
    private final WebClient webClient;
    private final String name;

    public OrganisasjonConnection(@Qualifier("ORGANISASJON") WebClient webClient,
            @Value("${spring.application.name:fpsoknad-mottak}") String name, OrganisasjonConfig cfg) {
        this.webClient = webClient;
        this.cfg = cfg;
        this.name = name;
    }

    @Override
    public String ping() {
        return webClient.get()
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block()
                .getBody();
    }

    @Override
    public URI pingEndpoint() {
        return URIUtil.uri(cfg.getBaseUri(), cfg.getPingPath());
    }

    String orgNavn(String orgnr) {
        LOG.trace("Henter orgnavn");
        var info = webClient.get()
                .uri(b -> b.pathSegment(cfg.getorganisasjonPath(), orgnr)
                        .build())
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(Map.class)
                .block()
                .getBody();
        return Optional.ofNullable(info)
                .map(OrganisasjonMapper::map)
                .orElse(orgnr);

    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + ", webClient=" + webClient + ", name=" + name + "]";
    }

}
