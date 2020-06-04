package no.nav.foreldrepenger.mottak.oppslag;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;
import no.nav.foreldrepenger.mottak.util.URIUtil;

@Component
public class ArbeidsforholdConnection implements PingEndpointAware {
    public static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdConnection.class);
    private final ArbeidsforholdConfig cfg;
    private final WebClient webClient;
    private final String name;

    public ArbeidsforholdConnection(@Qualifier("REST") WebClient webClient,
            @Value("${spring.application.name}") String name, ArbeidsforholdConfig cfg) {
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
        LOG.trace("Henter arbeidsforhold");
        var forhold = webClient.get()
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntityList(Map.class)
                .block()
                .getBody();
        LOG.trace("Hentet arbeidsforhold {}", forhold);
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
