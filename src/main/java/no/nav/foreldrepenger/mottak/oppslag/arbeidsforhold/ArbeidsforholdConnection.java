package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static java.time.LocalDate.now;
import static java.util.Comparator.comparing;
import static no.nav.foreldrepenger.common.util.Constants.TOKENX;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.ARBEIDSFORHOLD_LOGINSERVICE;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.ARBEIDSFORHOLD_TOKENX;
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
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Component
public class ArbeidsforholdConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdConnection.class);
    private final ArbeidsforholdConfig cfg;
    private final ArbeidsforholdMapper mapper;
    private final WebClient webClientTokenX;
    private final TokenUtil tokenUtil;

    public ArbeidsforholdConnection(@Qualifier(ARBEIDSFORHOLD_LOGINSERVICE) WebClient clientLoginservice,
                                    @Qualifier(ARBEIDSFORHOLD_TOKENX) WebClient webClientTokenX,
                                    TokenUtil tokenUtil, ArbeidsforholdConfig cfg, ArbeidsforholdMapper mapper) {
        super(clientLoginservice, cfg);
        this.webClientTokenX = webClientTokenX;
        this.tokenUtil = tokenUtil;
        this.cfg = cfg;
        this.mapper = mapper;
    }

    List<EnkeltArbeidsforhold> hentArbeidsforhold() {
        return hentArbeidsforhold(now().minus(cfg.getTidTilbake()), client());
    }

    private List<EnkeltArbeidsforhold> hentArbeidsforhold(LocalDate fom, WebClient client) {
        LOG.info("Henter arbeidsforhold for perioden fra {}", fom);
        var arbeidsforhold = client
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

    private WebClient client() {
        if (cfg.isBrukTokenX() && tokenUtil.harTokenFor(TOKENX)) {
            LOG.info("Mottak er kalt med TokenX og tokenx mot aareg er aktivert. Bruker Webclient med tokenx veksling.");
            return webClientTokenX;
        }
        return webClient;
    }

    @Override
    public String name() {
        return capitalize(ARBEIDSFORHOLD_LOGINSERVICE.toLowerCase());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + ", webClient=" + webClient + ", name=" + name() + "]";
    }

}
