package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static no.nav.foreldrepenger.mottak.domain.Orgnummer.MAGIC;
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
import no.nav.foreldrepenger.mottak.domain.Orgnummer;
import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;

@Component
public class OrganisasjonConnection extends AbstractWebClientConnection {

    private static final String MAGIC_ORG = "342352362";
    private static final String PRIVAT_ARBEIDSGIVER = "Privat arbeidsgiver";
    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonConnection.class);
    private final OrganisasjonConfig cfg;
    private PDLConnection oppslag;

    public OrganisasjonConnection(@Qualifier(ORGANISASJON) WebClient client, PDLConnection oppslag, OrganisasjonConfig cfg) {
        super(client, cfg);
        this.cfg = cfg;
        this.oppslag = oppslag;
    }

    @Cacheable(cacheNames = "organisasjon")
    public String navn(String orgnr) {
        if (isFnr(orgnr)) {
            return personNavn(Fødselsnummer.valueOf(orgnr));
        }
        if (isOrgnr(orgnr)) {
            return orgNavn(Orgnummer.valueOf(orgnr));
        }
        return "";
    }

    private static boolean isFnr(String nr) {
        return nr != null && nr.length() == 11;
    }

    private static boolean isOrgnr(String nr) {
        return nr != null && nr.length() == 9 && !nr.equals(MAGIC);
    }

    private String orgNavn(Orgnummer orgnr) {
        LOG.info("Henter organisasjonsnavn for {}", orgnr.maskert());
        try {
            var navn = Optional.ofNullable(webClient
                    .get()
                    .uri(b -> cfg.getOrganisasjonURI(b, orgnr.orgnr()))
                    .accept(APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Map.class)
                    .block()
                    .getBody())
                    .map(OrganisasjonMapper::tilOrganisasjonsnavn)
                    .filter(Objects::nonNull)
                    .orElse(orgnr.orgnr());
            LOG.info("Hentet organisasjonsnavn for {} OK", orgnr.maskert());
            LOG.trace("Organisasjonsnavn for {} er {}", orgnr, navn);
            return navn;
        } catch (Exception e) {
            LOG.warn("Fant ikke organisasjonsnavn for {}", orgnr.maskert());
            return orgnr.orgnr();
        }
    }

    private String personNavn(Fødselsnummer fnr) {
        LOG.info("Henter personnavn for {}", fnr);
        try {
            var n = oppslag.navnFor(fnr.getFnr());
            var navn = Optional.ofNullable(n)
                    .map(Navn::navn)
                    .orElse(PRIVAT_ARBEIDSGIVER);
            LOG.info("Hentet personnavn {} for {}", n.fornavn(), fnr);
            return navn;
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
        return getClass().getSimpleName() + "[cfg=" + cfg + ", name=" + name() + ", client=" + webClient + "]";
    }

}
