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

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public URI pingEndpoint() {
        return uri(config.getBaseUri(), config.getPingPath());
    }

    public SøknadWrapper hentSøknad(Lenke søknadsLenke) {
        LOG.trace("Henter søknad");
        URI uri = URI.create(config.getBaseUri() + søknadsLenke.getHref());
        return Optional.ofNullable(søknadsLenke)
                .map(s -> getForObject(uri, SøknadWrapper.class))
                .orElse(null);
    }

    public List<SakWrapper> hentSaker(String aktørId) {
        LOG.trace("Henter saker");
        return Optional.ofNullable(getForObject(uri(config.getBaseUri(), SAK,
                queryParams(AKTOR_ID, aktørId)), SakWrapper[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    public BehandlingWrapper hentBehandling(Lenke behandlingsLenke) {
        LOG.trace("Henter behandling");
        return getForObject(URI.create(config.getBaseUri() + behandlingsLenke.getHref()), BehandlingWrapper.class,
                false, true);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }

}
