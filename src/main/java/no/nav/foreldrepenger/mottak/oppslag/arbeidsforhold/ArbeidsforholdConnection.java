package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig.HISTORIKK;
import static no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig.SPORINGSINFORMASJON;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;
import no.nav.foreldrepenger.mottak.util.URIUtil;

@Component
public class ArbeidsforholdConnection implements PingEndpointAware {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdConnection.class);
    private final ArbeidsforholdConfig cfg;
    private final WebClient webClient;
    private final String name;
    private final ArbeidsforholdMapper mapper;

    public ArbeidsforholdConnection(@Qualifier("REST") WebClient webClient,
            @Value("${spring.application.name:fpsoknad-mottak}") String name, ArbeidsforholdConfig cfg,
            ArbeidsforholdMapper mapper) {
        this.webClient = webClient;
        this.cfg = cfg;
        this.name = name;
        this.mapper = mapper;
    }

    @Override
    public String ping() {
        return webClient.get()
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block()
                .getBody();
    }

    @Override
    public URI pingEndpoint() {
        return URIUtil.uri(cfg.getBaseUri(), cfg.getPingPath());
    }

    List<Arbeidsforhold> hentArbeidsforhold() {
        return hentArbeidsforhold(LocalDate.now().minusYears(5), LocalDate.now());
    }

    List<Arbeidsforhold> hentArbeidsforhold(LocalDate fom, LocalDate tom) {
        LOG.trace("Henter arbeidsgivere");
        return webClient.get()
                .uri(b -> b.path(cfg.getArbeidsforholdPath())
                        .queryParam(HISTORIKK, "true")
                        .queryParam(SPORINGSINFORMASJON, "false")
                        // .queryParam(ANSETTELSESPERIODE_FOM, fom.format(ISO_LOCAL_DATE))
                        // .queryParam(ANSETTELSESPERIODE_TOM, tom.format(ISO_LOCAL_DATE))
                        .build())
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
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + ", webClient=" + webClient + ", name=" + name + "]";
    }

}
