package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static no.nav.foreldrepenger.common.domain.Orgnummer.MAGIC;
import static no.nav.foreldrepenger.mottak.http.RetryAwareWebClientConfiguration.retryOnlyOn5xxFailures;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.ORGANISASJON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StringUtils.capitalize;

import java.util.Optional;

import no.nav.foreldrepenger.mottak.http.WebClientRetryAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto.OrganisasjonsNavnDTO;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;

@Component
public class OrganisasjonConnection extends AbstractWebClientConnection {

    private static final String PRIVAT_ARBEIDSGIVER = "Privat arbeidsgiver";
    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonConnection.class);
    private final OrganisasjonConfig cfg;
    private final PDLConnection oppslag;

    public OrganisasjonConnection(@Qualifier(ORGANISASJON) WebClient client, PDLConnection oppslag, OrganisasjonConfig cfg) {
        super(client, cfg);
        this.cfg = cfg;
        this.oppslag = oppslag;
    }

    @Cacheable(cacheNames = "organisasjon")
    public String navn(String identifikator) {
        if (isFnr(identifikator)) {
            return personNavn(new Fødselsnummer(identifikator));
        }
        if (isOrgnr(identifikator)) {
            return orgNavn(Orgnummer.valueOf(identifikator));
        }
        return "";
    }

    private static boolean isFnr(String nr) {
        return nr != null && nr.length() == 11;
    }

    private static boolean isOrgnr(String nr) {
        return nr != null && nr.length() == 9 && !nr.equals(MAGIC);
    }

    @WebClientRetryAware
    private String orgNavn(Orgnummer orgnr) {
        LOG.info("Henter organisasjonsnavn for {}", orgnr.maskert());
        var navn = webClient.get()
            .uri(b -> cfg.getOrganisasjonURI(b, orgnr.value()))
            .accept(APPLICATION_JSON)
            .retrieve()
            .bodyToMono(OrganisasjonsNavnDTO.class)
//            .retryWhen(retryOnlyOn5xxFailures(cfg.getBaseUri().toString()))
            .mapNotNull(OrganisasjonsNavnDTO::tilOrganisasjonsnavn)
            .defaultIfEmpty(orgnr.value())
            .doOnError(throwable -> LOG.warn("Fant ikke organisasjonsnavn for {}. Returnerer orgnummer som navn.", orgnr.maskert(), throwable))
            .onErrorReturn(orgnr.value())
            .block();
        LOG.info("Hentet organisasjonsnavn for {} OK", orgnr.maskert());
        LOG.trace("Organisasjonsnavn for {} er {}", orgnr, navn);
        return navn;
    }

    private String personNavn(Fødselsnummer fnr) {
        LOG.info("Henter personnavn for {}", fnr);
        try {
            var n = oppslag.navnFor(fnr.value());
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
