package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static java.time.LocalDate.now;
import static java.util.Comparator.comparing;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.ARBEIDSFORHOLD;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StringUtils.capitalize;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;

@Component
public class ArbeidsforholdConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdConnection.class);
    private final ArbeidsforholdConfig cfg;
    private final ArbeidsforholdMapper mapper;

    public ArbeidsforholdConnection(@Qualifier(ARBEIDSFORHOLD) WebClient clientSts,
                                    ArbeidsforholdConfig cfg, ArbeidsforholdMapper mapper) {
        super(clientSts, cfg);
        this.cfg = cfg;
        this.mapper = mapper;
    }

    List<EnkeltArbeidsforhold> hentArbeidsforhold() {
        return hentArbeidsforhold(now().minus(cfg.getTidTilbake()));
    }

    private List<EnkeltArbeidsforhold> hentArbeidsforhold(LocalDate fom) {
        LOG.info("Henter arbeidsforhold for perioden fra {}", fom);
        var arbeidsforhold = webClient
                .get()
                .uri(b -> cfg.getArbeidsforholdURI(b, fom))
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, ClientResponse::createException)
                .toEntityList(Map.class)
                .block()
                .getBody()
                .stream()
                .map(mapper::tilArbeidsforhold)
                .sorted(comparing(EnkeltArbeidsforhold::getArbeidsgiverNavn))
                .toList();
        LOG.info("Hentet {} arbeidsforhold for perioden fra {}", arbeidsforhold.size(), fom);
        LOG.trace("Arbeidsforhold: {}", arbeidsforhold);
        return arbeidsforhold;
    }

    @Override
    public String name() {
        return capitalize(ARBEIDSFORHOLD.toLowerCase());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + ", webClient=" + webClient + ", name=" + name() + "]";
    }

}
