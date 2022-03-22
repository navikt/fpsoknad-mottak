package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static java.time.LocalDate.now;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.ARBEIDSFORHOLD;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StringUtils.capitalize;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto.ArbeidsforholdDTO;

@Component
public class ArbeidsforholdConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdConnection.class);
    private final ArbeidsforholdConfig cfg;

    public ArbeidsforholdConnection(@Qualifier(ARBEIDSFORHOLD) WebClient client, ArbeidsforholdConfig cfg) {
        super(client, cfg);
        this.cfg = cfg;
    }

    List<ArbeidsforholdDTO> hentArbeidsforhold() {
        return hentArbeidsforhold(now().minus(cfg.getTidTilbake()));
    }

    private List<ArbeidsforholdDTO> hentArbeidsforhold(LocalDate fom) {
        LOG.info("Henter arbeidsforhold for perioden fra {}", fom);
        var arbeidsforhold = webClient.get()
            .uri(b -> cfg.getArbeidsforholdURI(b, fom))
            .accept(APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(ArbeidsforholdDTO.class)
            .collectList()
            .blockOptional()
            .orElse(List.of());

        LOG.info("Hentet {} arbeidsforhold for perioden fra {}", arbeidsforhold.size(), fom);
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
