package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.emptyList;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.http.PingEndpointAware;
import no.nav.foreldrepenger.mottak.innsyn.dto.BehandlingDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SakDTO;
import no.nav.foreldrepenger.mottak.innsyn.fpinfov2.Saker;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.dto.UttaksplanDTO;

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
        return config.pingEndpoint();
    }

    @Override
    public String name() {
        return config.name();
    }

    List<SakDTO> saker(String aktørId) {
        LOG.trace("Henter saker for {}", aktørId);
        return Optional.ofNullable(
                getForObject(config.sakURI(aktørId), SakDTO[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    Saker sakerV2(String aktørId) {
        LOG.trace("Henter sakerV2 for {}", aktørId);
        return Optional.ofNullable(getForObject(config.sakV2URI(aktørId), Saker.class))
            .orElseThrow();
    }

    UttaksplanDTO uttaksplan(String saksnummer) {
        LOG.trace("Henter uttaksplan for sak {}", saksnummer);
        return getForObject(config.uttaksplanURI(saksnummer), UttaksplanDTO.class);
    }

    UttaksplanDTO uttaksplan(AktørId aktørId, AktørId annenPart) {
        LOG.trace("Henter uttaksplan for {} med annen part {}", aktørId, annenPart);
        try {
            return getForObject(config.uttaksplanURI(aktørId, annenPart), UttaksplanDTO.class);
        } catch (Exception e) {
            LOG.warn("Kunne ikke hente uttaksplan for annen part {}", annenPart, e);
            return null;
        }
    }

    BehandlingDTO behandling(Lenke lenke) {
        return hent(lenke, BehandlingDTO.class);
    }

    private <T> T hent(Lenke lenke, Class<T> clazz) {
        return Optional.ofNullable(lenke)
                .map(Lenke::href)
                .filter(Objects::nonNull)
                .map(l -> getForObject(config.createLink(l), clazz))
                .orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }

}
