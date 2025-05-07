package no.nav.foreldrepenger.mottak.oversikt;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.FPOVERSIKT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import reactor.core.publisher.Mono;

@Component
public class OversiktConnection {

    private static final Logger LOG = LoggerFactory.getLogger(OversiktConnection.class);
    private final WebClient webClient;
    private final OversiktConfig cfg;

    public OversiktConnection(@Qualifier(FPOVERSIKT) WebClient client, OversiktConfig cfg) {
        this.webClient = client;
        this.cfg = cfg;
    }

    PersonDto hentPersoninfo(Ytelse ytelse) {
        return webClient.get()
                .uri(cfg.personOppslagURI(ytelse))
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PersonDto.class)
                .block();
    }

    List<EnkeltArbeidsforhold> hentArbeidsforhold() {
        LOG.info("Henter arbeidsforhold");
        var arbeidsforhold = webClient.get()
            .uri(cfg.mineArbeidsforholdURI())
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, response ->
                response.bodyToMono(String.class).map(body -> {
                    LOG.info("Feil fra server: " + body);
                    throw new RuntimeException("Feilstatus: " + response.statusCode());
                }))
            .bodyToFlux(EnkeltArbeidsforhold.class)
            .collectList()
            .block();

        LOG.info("Hentet {} arbeidsforhold", arbeidsforhold.size());
        return arbeidsforhold;
    }

    AktørId aktørId(Fødselsnummer fnr) {
        LOG.info("Henter aktørid for {}", fnr);
        var aktørId = webClient.post()
            .uri(cfg.aktørid())
            .body(Mono.just(new AnnenpartAktørIdRequest(fnr)), AnnenpartAktørIdRequest.class)
            .retrieve()
            .bodyToMono(AktørId.class)
            .block();
        LOG.info("Hentet aktørid {} for {}", aktørId, fnr);
        return aktørId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + ", webClient=" + webClient + "]";
    }
}
