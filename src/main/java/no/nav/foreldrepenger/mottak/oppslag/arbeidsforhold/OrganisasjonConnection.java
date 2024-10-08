package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.ORGANISASJON;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.mottak.http.Retry;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto.OrganisasjonsNavnDTO;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;

@Component
public class OrganisasjonConnection {

    private static final String PRIVAT_ARBEIDSGIVER = "Privat arbeidsgiver";
    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonConnection.class);
    private final WebClient webClient;
    private final OrganisasjonConfig cfg;
    private final PDLConnection oppslag;

    public OrganisasjonConnection(@Qualifier(ORGANISASJON) WebClient client, PDLConnection oppslag, OrganisasjonConfig cfg) {
        this.webClient = client;
        this.cfg = cfg;
        this.oppslag = oppslag;
    }

    @Cacheable(cacheNames = "organisasjon")
    public String navn(String identifikator) {
        if (isFnr(identifikator)) {
            return personNavn(new Fødselsnummer(identifikator));
        }
        if (isOrgnr(identifikator)) {
            return orgNavn(new Orgnummer(identifikator));
        }
        return "";
    }

    private static boolean isFnr(String nr) {
        return nr != null && nr.length() == 11;
    }

    private static boolean isOrgnr(String nr) {
        return nr != null && nr.length() == 9;
    }

    public String orgNavn(Orgnummer orgnr) {
        try {
            LOG.info("Henter organisasjonsnavn for {}", orgnr.maskert());
            return organisasjonsNavn(orgnr);
        } catch (Exception e) {
            LOG.warn("Fant ikke organisasjonsnavn for {}. Returnerer orgnummer som navn.", orgnr.maskert(), e);
            return orgnr.value();
        }
    }

    @Retry
    private String organisasjonsNavn(Orgnummer orgnr) {
        var navn = webClient.get()
            .uri(b -> cfg.getOrganisasjonURI(b, orgnr.value()))
            .accept(APPLICATION_JSON)
            .retrieve()
            .bodyToMono(OrganisasjonsNavnDTO.class)
            .mapNotNull(OrganisasjonsNavnDTO::tilOrganisasjonsnavn)
            .defaultIfEmpty(orgnr.value())
            .block();
        LOG.info("Hentet organisasjonsnavn for {} OK", orgnr.maskert());
        LOG.trace("Organisasjonsnavn for {} er {}", orgnr, navn);
        return navn;
    }

    private String personNavn(Fødselsnummer fnr) {
        LOG.info("Henter personnavn");
        try {
            var n = oppslag.navnFor(fnr.value());
            var navn = Optional.ofNullable(n)
                .map(Navn::navn)
                .orElse(PRIVAT_ARBEIDSGIVER);
            LOG.info("Hentet personnavn {} for oppgitt fnr",
                n != null ? n.fornavn() : PRIVAT_ARBEIDSGIVER);
            return navn;
        } catch (Exception e) {
            LOG.warn("Fant ikke personnavn. Returneren {} som navn.", PRIVAT_ARBEIDSGIVER, e);
            return PRIVAT_ARBEIDSGIVER;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + ", client=" + webClient + "]";
    }

}
