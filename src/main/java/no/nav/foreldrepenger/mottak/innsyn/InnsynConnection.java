package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.AKTOR_ID;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.SAK;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.innsending.AbstractRestConnection;

@Component
public class InnsynConnection extends AbstractRestConnection {
    private static final Logger LOG = LoggerFactory.getLogger(InnsynConnection.class);

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

    public SøknadWrapper hentSøknad(Lenke søknadsLenke) {
        return Optional.ofNullable(søknadsLenke)
                .map(s -> safeGet(s, SøknadWrapper.class, true, false))
                .orElse(null);
    }

    public List<SakWrapper> hentSaker(String aktørId) {
        LOG.trace("Henter saker for {}", aktørId);
        return Optional.ofNullable(getForObject(uri(config.getBaseUri(), SAK,
                queryParams(AKTOR_ID, aktørId)), SakWrapper[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    public BehandlingWrapper hentBehandling(Lenke behandlingsLenke) {
        URI uri = URI.create(config.getBaseUri() + behandlingsLenke.getHref());
        LOG.trace("Henter behandling fra {}", uri);
        return getForObject(uri, BehandlingWrapper.class, false, true);
    }

    private <T> T safeGet(Lenke lenke, Class<T> clazz, boolean confidential, boolean doThrow) {
        URI uri = URI.create(config.getBaseUri() + lenke.getHref());
        LOG.trace("Henter søknad fra {}", uri);
        return getForObject(uri, clazz, confidential, doThrow);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }
}
