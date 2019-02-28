package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.AKTOR_ID;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.SAK;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.SAKSNUMMER;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.UTTAKSPLAN;
import static no.nav.foreldrepenger.mottak.util.URIUtil.queryParams;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.PingEndpointAware;
import no.nav.foreldrepenger.mottak.innsyn.dto.BehandlingDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SakDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SøknadDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.VedtakDTO;

@Component
public class InnsynConnection extends AbstractRestConnection implements PingEndpointAware {
    private static final Logger LOG = LoggerFactory.getLogger(InnsynConnection.class);

    private final InnsynConfig config;

    public InnsynConnection(RestOperations restOperations, InnsynConfig config) {
        super(restOperations);
        this.config = config;
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    @Override
    public URI pingEndpoint() {
        return uri(config.getUri(), config.getPingPath());
    }

    public List<SakDTO> hentSaker(String aktørId) {
        LOG.trace("Henter saker for {}", aktørId);
        return Optional.ofNullable(
                getForObject(uri(config.getUri(), SAK, queryParams(AKTOR_ID, aktørId)), SakDTO[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    public List<UttaksPeriode> hentUttaksplan(String saksnummer) {
        LOG.trace("Henter uttaksplan for sak {}", saksnummer);
        return Optional.ofNullable(
                getForObject(uri(config.getUri(), UTTAKSPLAN, queryParams(SAKSNUMMER, saksnummer)),
                        UttaksPeriode[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    public BehandlingDTO hentBehandling(Lenke behandlingsLenke) {
        return hent(behandlingsLenke, BehandlingDTO.class);
    }

    public VedtakDTO hentVedtak(Lenke vedtaksLenke) {
        return hent(vedtaksLenke, VedtakDTO.class);
    }

    public SøknadDTO hentSøknad(Lenke søknadsLenke) {
        return hent(søknadsLenke, SøknadDTO.class);
    }

    private <T> T hent(Lenke lenke, Class<T> clazz) {
        if (lenke != null && lenke.getHref() != null) {
            LOG.trace("Henter {} fra {}", clazz.getSimpleName(), lenke.getHref());
            return Optional.ofNullable(
                    getForObject(URI.create(config.getUri() + lenke.getHref()), clazz))
                    .orElse(null);
        }
        LOG.trace("Henter ingenting");
        return null;
    }

    @Override
    public String name() {
        return "fpinfo";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }
}
