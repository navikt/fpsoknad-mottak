package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.config.WebClientConfiguration.ARBEIDSFORHOLD;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;

@Component
public class ArbeidsforholdConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdConnection.class);
    private final ArbeidsforholdConfig cfg;
    private final ArbeidsforholdMapper mapper;

    public ArbeidsforholdConnection(@Qualifier(ARBEIDSFORHOLD) WebClient webClient,
            @Value("${spring.application.name:fpsoknad-mottak}") String name, ArbeidsforholdConfig cfg,
            ArbeidsforholdMapper mapper) {
        super(webClient, cfg, name);
        this.cfg = cfg;
        this.mapper = mapper;
    }

    List<EnkeltArbeidsforhold> hentArbeidsforhold() {
        return hentArbeidsforhold(now().minus(cfg.getTidTilbake()), now());
    }

    private List<EnkeltArbeidsforhold> hentArbeidsforhold(LocalDate fom, LocalDate tom) {
        LOG.trace("Henter arbeidsforhold for {} -> {}", fom, tom);
        return getWebClient().get()
                .uri(b -> cfg.getArbeidsforholdURI(b, fom, tom))
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntityList(Map.class)
                .block()
                .getBody()
                .stream()
                .map(mapper::map)
                .collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + ", webClient=" + getWebClient() + ", name=" + name() + "]";
    }

}
