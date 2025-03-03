package no.nav.foreldrepenger.mottak.oppslag.dkif;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.mottak.http.Retry;
import no.nav.foreldrepenger.mottak.http.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.KRR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class DigdirKrrProxyConnection {
    private static final Logger LOG = LoggerFactory.getLogger(DigdirKrrProxyConnection.class);
    private final WebClient webClient;
    private final DigdirKrrProxyConfig cfg;
    private final TokenUtil tokenUtil;

    public DigdirKrrProxyConnection(@Qualifier(KRR) WebClient client, DigdirKrrProxyConfig cfg, TokenUtil tokenUtil) {
        this.webClient = client;
        this.cfg = cfg;
        this.tokenUtil = tokenUtil;
    }

    public Målform målform() {
        try {
            LOG.info("Henter målform fra digdir-krr-proxy");
            return hentMålform();
        } catch (Exception e) {
            LOG.warn("DKIF oppslag målform feilet. Forsetter med default målform NB.", e);
            return Målform.standard();
        }
    }

    @Retry
    private Målform hentMålform() {
        var fødselsnummer = tokenUtil.autentisertBrukerOrElseThrowException();
        var respons = webClient.post()
                .uri(uri -> cfg.kontaktUri())
                .body(Mono.just(new Personidenter(List.of(fødselsnummer))), Personidenter.class)
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Kontaktinformasjoner.class)
                .defaultIfEmpty(new Kontaktinformasjoner(Map.of(), Map.of(fødselsnummer, Kontaktinformasjoner.FeilKode.noen_andre)))
                .timeout(Duration.ofSeconds(5))
                .block();
        if (respons.feil() != null && !respons.feil().isEmpty()) {
            LOG.warn("Feil ved henting av målform fra DKIF: {}. Forsetter med default målform NB", respons.feil().get(fødselsnummer));
            return Målform.standard();
        }
        var person = respons.personer().get(fødselsnummer);
        if (person.aktiv()) {
            return person.spraak();
        } else {
            LOG.info("Personen finnes i PDL men mangler kontaktinfo i KRR. Forsetter med default målform NB");
            return Målform.standard();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [cfg=" + cfg + "]";
    }

    private record Personidenter(List<Fødselsnummer> personidenter) {
    }
}
