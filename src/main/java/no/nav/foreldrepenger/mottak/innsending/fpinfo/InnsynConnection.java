package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConfig.AKTOR_ID;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConfig.BEHANDLING;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConfig.BEHANDLING_ID;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConfig.SAK;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConfig.SØKNAD;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.innsending.AbstractRestConnection;

@Component
public class InnsynConnection extends AbstractRestConnection {

    private final InnsynConfig config;

    public InnsynConnection(RestTemplate template, InnsynConfig config) {
        super(template);
        this.config = config;
    }

    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public URI pingEndpoint() {
        return uri(config.getBaseUri(), config.getPingPath());
    }

    public SøknadWrapper hentSøknad(String behandlingId) {
        return getForObject(uri(config.getBaseUri(), SØKNAD,
                queryParams(BEHANDLING_ID, behandlingId)), SøknadWrapper.class);
    }

    public List<SakWrapper> hentSaker(String aktørId) {
        return Optional.ofNullable(getForObject(uri(config.getBaseUri(), SAK,
                queryParams(AKTOR_ID, aktørId)), SakWrapper[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    public Behandling hentBehandling(String behandlingId) {
        return withId(getForObject(uri(config.getBaseUri(), BEHANDLING,
                queryParams(BEHANDLING_ID, behandlingId)), Behandling.class, false, true), behandlingId);
    }

    public Behandling hentBehandling(Lenke lenke) {
        URI uri = URI.create(config.getBaseUri() + lenke.getHref());
        return withId(getForObject(uri, Behandling.class, false, true), uri);
    }

    private static Behandling withId(Behandling behandling, URI uri) {
        return withId(behandling, id(uri));
    }

    private static Behandling withId(Behandling behandling, String id) {
        return Optional.ofNullable(behandling)
                .map(s -> s.withId(id))
                .orElse(null);
    }

    private static String id(URI uri) {
        return fromUri(uri).build().getQueryParams().getFirst(BEHANDLING_ID);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }
}
