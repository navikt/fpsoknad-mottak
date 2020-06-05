package no.nav.foreldrepenger.mottak.oppslag;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static no.nav.foreldrepenger.mottak.oppslag.ArbeidsforholdConfig.ANSETTELSESPERIODE_FOM;
import static no.nav.foreldrepenger.mottak.oppslag.ArbeidsforholdConfig.ANSETTELSESPERIODE_TOM;
import static no.nav.foreldrepenger.mottak.oppslag.ArbeidsforholdConfig.HISTORIKK;
import static no.nav.foreldrepenger.mottak.oppslag.ArbeidsforholdConfig.SPORINGSINFORMASJON;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;
import no.nav.foreldrepenger.mottak.util.JacksonWrapper;
import no.nav.foreldrepenger.mottak.util.URIUtil;

@Component
public class ArbeidsforholdConnection implements PingEndpointAware {

    public static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdConnection.class);
    private final ArbeidsforholdConfig cfg;
    private final WebClient webClient;
    private final String name;
    @Inject
    private JacksonWrapper mapper;

    public ArbeidsforholdConnection(@Qualifier("REST") WebClient webClient,
            @Value("${spring.application.name:fpsoknad-mottak}") String name, ArbeidsforholdConfig cfg) {
        this.webClient = webClient;
        this.cfg = cfg;
        this.name = name;
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
        LOG.trace("Henter arbeidsforhold");
        var forhold = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(cfg.getArbeidsforholdPath())
                        .queryParam(HISTORIKK, "true")
                        .queryParam(SPORINGSINFORMASJON, "false")
                        .queryParam(ANSETTELSESPERIODE_FOM, fom.format(ISO_LOCAL_DATE))
                        .queryParam(ANSETTELSESPERIODE_TOM, tom.format(ISO_LOCAL_DATE))
                        .build())
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class) // TODO
                .block()
                .getBody();
        LOG.trace("Hentet arbeidsforhold {}", mapper.writeValueAsString(forhold));
        return Collections.emptyList(); // TODO
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
