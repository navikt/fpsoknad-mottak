package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.ORGANISASJON;
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

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import no.nav.foreldrepenger.mottak.oppslag.OppslagConnection;

@Component
public class OrganisasjonConnection extends AbstractWebClientConnection {

    private static final String PRIVAT_ARBEIDSGIVER = "Privat arbeidsgiver";
    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonConnection.class);
    private final OrganisasjonConfig cfg;
    private OppslagConnection oppslag;

    public OrganisasjonConnection(@Qualifier(ORGANISASJON) WebClient client, OppslagConnection oppslag, OrganisasjonConfig cfg) {
        super(client, cfg);
        this.cfg = cfg;
        this.oppslag = oppslag;
    }

    @Cacheable(cacheNames = "organisasjon")
    public String navn(String orgnr) {
        if (orgnr != null && orgnr.length() == 11) {
            return personNavn(Fødselsnummer.valueOf(orgnr));
        }
        if (orgnr != null && orgnr.length() == 9) {
            return orgNavn(orgnr);
        }
        return "";
    }

    private String orgNavn(String orgnr) {
        LOG.info("Henter organisasjonsnavn for {}", orgnr);
        try {
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
            LOG.info("Hentet organisasjonsnavn {} for {}", navn, orgnr);
            return navn;
        } catch (Exception e) {
            LOG.warn("Fant ikke organisasjonsnavn for {}", orgnr);
            return orgnr;
        }
    }

    private String personNavn(Fødselsnummer fnr) {
        LOG.info("Henter personnavn for {}", fnr);
        try {
            return Optional.ofNullable(oppslag.navn(fnr))
                    .map(Navn::navn)
                    .orElse(PRIVAT_ARBEIDSGIVER);
        } catch (Exception e) {
            LOG.warn("Fant ikke personnavn for {}", fnr);
            return PRIVAT_ARBEIDSGIVER;
        }
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
