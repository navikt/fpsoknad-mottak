package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import static no.nav.foreldrepenger.mottak.oppslag.WebClientConfiguration.ORGANISASJON;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.oppslag.AbstractWebClientConnection;

@Component
public class OrganisasjonConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonConnection.class);
    private final OrganisasjonConfig cfg;

    public OrganisasjonConnection(@Qualifier(ORGANISASJON) WebClient webClient,
            @Value("${spring.application.name:fpsoknad-mottak}") String name, OrganisasjonConfig cfg) {
        super(webClient, cfg, name);
        this.cfg = cfg;
    }

    public String organisasjonsNavn(String orgnr) {
        LOG.trace("Henter organisasjonsnavn for {}", orgnr);
        return Optional.ofNullable(getWebClient().get()
                .uri(cfg::getOrganisasjonURI)
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(Map.class)
                .block()
                .getBody())
                .map(OrganisasjonMapper::map)
                .filter(Objects::nonNull)
                .orElse(orgnr);

    }

}
