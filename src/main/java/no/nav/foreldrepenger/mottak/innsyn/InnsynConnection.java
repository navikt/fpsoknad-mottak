package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.emptyList;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;
import no.nav.foreldrepenger.common.innsyn.v2.Saksnummer;
import no.nav.foreldrepenger.common.innsyn.v2.VedtakPeriode;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.http.PingEndpointAware;
import no.nav.foreldrepenger.mottak.innsyn.dto.BehandlingDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.LenkeDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SakDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.UttaksplanDTO;

@Component
public class InnsynConnection extends AbstractRestConnection implements PingEndpointAware {
    private static final Logger LOG = LoggerFactory.getLogger(InnsynConnection.class);
    private final InnsynConfig cfg;

    public InnsynConnection(RestOperations restOperations, InnsynConfig cfg) {
        super(restOperations);
        this.cfg = cfg;
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    @Override
    public URI pingEndpoint() {
        return cfg.pingEndpoint();
    }

    @Override
    public String name() {
        return cfg.name();
    }

    List<SakDTO> saker(String aktørId) {
        LOG.trace("Henter saker for {}", aktørId);
        return Optional.ofNullable(
                getForObject(cfg.sakURI(aktørId), SakDTO[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    Saker sakerV2(AktørId aktørId) {
        LOG.trace("Henter sakerV2 for {}", aktørId);
        return Optional.ofNullable(getForObject(cfg.sakV2URI(aktørId.value()), Saker.class))
            .orElseThrow();
    }

    public List<VedtakPeriode> annenPartsVedtaksperioder(AktørId søker,
                                                         AktørId annenForelder,
                                                         AktørId barn) {
        return Optional.ofNullable(getForObject(cfg.annenPartsVedtaksperioderURI(søker.value(), annenForelder.value(),
                barn.value()), VedtakPeriode[].class))
            .map(Arrays::asList)
            .orElse(emptyList());
    }

    UttaksplanDTO uttaksplan(Saksnummer saksnummer) {
        LOG.trace("Henter uttaksplan");
        return getForObject(cfg.uttaksplanURI(saksnummer), UttaksplanDTO.class);
    }

    UttaksplanDTO uttaksplan(AktørId aktørId, AktørId annenPart) {
        LOG.trace("Henter uttaksplan for {} med annen part {}", aktørId, annenPart);
        try {
            return getForObject(cfg.uttaksplanURI(aktørId, annenPart), UttaksplanDTO.class);
        } catch (Exception e) {
            LOG.warn("Kunne ikke hente uttaksplan for annen part {}", annenPart, e);
            return null;
        }
    }

    BehandlingDTO behandling(LenkeDTO lenke) {
        return hent(lenke);
    }

    private BehandlingDTO hent(LenkeDTO lenke) {
        return Optional.ofNullable(lenke)
            .map(LenkeDTO::href)
            .map(l -> getForObject(cfg.createLink(l), BehandlingDTO.class, true))
            .orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + cfg + "]";
    }
}
