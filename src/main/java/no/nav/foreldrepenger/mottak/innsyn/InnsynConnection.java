package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.AKTOR_ID;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.ANNENFORELDERPLAN;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.ANNENPART;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.BRUKER;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.SAK;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.SAKSNUMMER;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.UTTAKSPLAN;
import static no.nav.foreldrepenger.mottak.util.URIUtil.queryParams;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;
import no.nav.foreldrepenger.mottak.innsyn.dto.BehandlingDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SakDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SøknadDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.VedtakDTO;
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
        return uri(config.getUri(), config.getPingPath());
    }

    public List<SakDTO> saker(String aktørId) {
        LOG.trace("Henter saker for {}", aktørId);
        return Optional.ofNullable(
                getForObject(uri(config.getUri(), SAK, queryParams(AKTOR_ID, aktørId)), SakDTO[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    public UttaksplanDTO uttaksplan(String saksnummer) {
        LOG.trace("Henter uttaksplan for sak {}", saksnummer);
        return getForObject(uri(config.getUri(), UTTAKSPLAN, queryParams(SAKSNUMMER, saksnummer)), UttaksplanDTO.class);
    }

    public UttaksplanDTO uttaksplan(AktørId aktørId, AktørId annenPart) {
        LOG.trace("Henter uttaksplan for {} med annen part {}", aktørId, annenPart);
        return getForObject(
                uri(config.getUri(), ANNENFORELDERPLAN,
                        queryParams(ANNENPART, annenPart.getId(), BRUKER, aktørId.getId())),
                UttaksplanDTO.class);
    }

    public BehandlingDTO behandling(Lenke lenke) {
        return hent(lenke, BehandlingDTO.class);
    }

    public VedtakDTO vedtak(Lenke lenke) {
        return hent(lenke, VedtakDTO.class);
    }

    public SøknadDTO søknad(Lenke lenke) {
        return hent(lenke, SøknadDTO.class);
    }

    private <T> T hent(Lenke lenke, Class<T> clazz) {
        return Optional.ofNullable(lenke)
                .map(Lenke::getHref)
                .filter(Objects::nonNull)
                .map(l -> getForObject(URI.create(config.getUri() + l), clazz))
                .orElse(null);
    }

    @Override
    public String name() {
        return config.getUri().getHost();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }

}
