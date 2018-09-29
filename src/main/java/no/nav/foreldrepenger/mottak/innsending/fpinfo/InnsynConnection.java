package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import static no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConfig.AKTOR_ID;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConfig.BEHANDLING_ID;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConfig.BEHANDLING_PATH;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConfig.SAK_PATH;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConfig.SØKNAD_PATH;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public SøknadWrapper hentSøknad(String behandlingId) {
        return getForObject(uri(config.getBaseUri(), SØKNAD_PATH,
                queryParams(BEHANDLING_ID, behandlingId)), SøknadWrapper.class);
    }

    public List<SakStatusWrapper> hentSaker(String aktørId) {
        SakStatusWrapper[] saker = getForObject(uri(config.getBaseUri(), SAK_PATH,
                queryParams(AKTOR_ID, aktørId)), SakStatusWrapper[].class);
        return saker == null ? Collections.emptyList() : Arrays.asList(saker);
    }

    public Behandling hentBehandling(String behandlingId) {
        return withID(getForObject(uri(config.getBaseUri(), BEHANDLING_PATH,
                queryParams(BEHANDLING_ID, behandlingId)), Behandling.class), behandlingId);
    }

    public Behandling hentBehandling(Lenke lenke) {
        URI uri = URI.create(config.getBaseUri() + lenke.getHref());
        return withID(getForObject(uri, Behandling.class), uri);
    }

    private static Behandling withID(Behandling behandling, URI uri) {
        return withID(behandling, fromUri(uri).build().getQueryParams().getFirst(BEHANDLING_ID));
    }

    private static Behandling withID(Behandling behandling, String id) {
        if (behandling == null) {
            return null;
        }
        behandling.setId(id);
        return behandling;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }

}
