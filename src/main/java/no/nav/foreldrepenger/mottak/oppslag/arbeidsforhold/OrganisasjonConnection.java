package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static no.nav.foreldrepenger.mottak.config.WebClientConfiguration.ORGANISASJON;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;

@Component
public class OrganisasjonConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonConnection.class);
    private final OrganisasjonConfig cfg;

    public OrganisasjonConnection(@Qualifier(ORGANISASJON) WebClient webClient,
            OrganisasjonConfig cfg) {
        super(webClient, cfg);
        this.cfg = cfg;
    }

    public String organisasjonsNavn(String orgnr) {
        LOG.trace("Henter organisasjonsnavn for {}", orgnr);
        return Optional.ofNullable(getWebClient()
                .get()
                .uri(b -> cfg.getOrganisasjonURI(b, orgnr))
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(Map.class)
                .block()
                .getBody())
                .map(OrganisasjonMapper::map)
                .filter(Objects::nonNull)
                .orElse(orgnr);
    }

    @Override
    public String name() {
        return "Organisasjon";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + ", name()=" + name() + ", getWebClient()=" + getWebClient()
                + "]";
    }

}
