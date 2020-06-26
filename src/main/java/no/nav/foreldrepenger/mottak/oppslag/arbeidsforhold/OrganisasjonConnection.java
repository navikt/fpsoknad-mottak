package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static no.nav.foreldrepenger.mottak.config.WebClientConfiguration.ORGANISASJON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StringUtils.capitalize;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;

@Component
public class OrganisasjonConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonConnection.class);
    private final OrganisasjonConfig cfg;

    public OrganisasjonConnection(@Qualifier(ORGANISASJON) WebClient client, OrganisasjonConfig cfg) {
        super(client, cfg);
        this.cfg = cfg;
    }

    @Cacheable(cacheNames = "organisasjon")
    public String organisasjonsNavn(String orgnr) {
        LOG.trace("Henter organisasjonsnavn for {}", orgnr);
        var navn = Optional.ofNullable(getWebClient()
                .get()
                .uri(b -> cfg.getOrganisasjonURI(b, orgnr))
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(Map.class)
                .block()
                .getBody())
                .map(OrganisasjonMapper::tilOrganisasjonsnavn)
                .filter(Objects::nonNull)
                .orElse(orgnr);
        LOG.trace("Hentet organisasjonsnavn {} for {}", navn, orgnr);
        return navn;
    }

    @Override
    public String name() {
        return capitalize(ORGANISASJON.toLowerCase());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + ", name=" + name() + ", client=" + getWebClient() + "]";
    }

}
