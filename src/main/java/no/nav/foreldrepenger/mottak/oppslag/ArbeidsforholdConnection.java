package no.nav.foreldrepenger.mottak.oppslag;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;

@Component
public class ArbeidsforholdConnection implements PingEndpointAware {
    public static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdConnection.class);
    private final ArbeidsforholdConfig cfg;
    private final WebClient webClient;

    public ArbeidsforholdConnection(@Qualifier("REST") WebClient webClient, ArbeidsforholdConfig cfg) {
        this.webClient = webClient;
        this.cfg = cfg;
    }

    @Override
    public String ping() {
        return "OK";
    }

    @Override
    public URI pingEndpoint() {
        return URI.create("http://www.vg.no");
    }

    List<Arbeidsforhold> hentArbeidsforhold() {
        LOG.trace("Henter arbeidsforhold");
        var forhold = webClient.get()
                .accept(APPLICATION_JSON)
                .retrieve().toEntityList(Arbeidsforhold.class).block().getBody();
        LOG.trace("Hentet arbeidsforhold {}", forhold);
        return Collections.emptyList();
    }

    @Override
    public String name() {
        return "fpsoknad-mottak";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + ", webClient=" + webClient + "]";
    }
}
