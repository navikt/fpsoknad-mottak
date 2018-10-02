package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.XMLTilSøknadMapper;

@Service
public class InnsynTjeneste implements Innsyn {

    private static final Logger LOG = LoggerFactory.getLogger(InnsynTjeneste.class);

    private final XMLTilSøknadMapper mapper;
    private final InnsynConnection connection;

    public InnsynTjeneste(InnsynConnection connection, XMLTilSøknadMapper mapper) {
        this.connection = connection;
        this.mapper = mapper;
    }

    @Override
    public List<Sak> hentSaker(AktorId aktørId) {
        return hentSaker(aktørId.getId());
    }

    @Override
    public List<Sak> hentSaker(String aktørId) {
        LOG.info("Henter saker for {}", aktørId);
        List<Sak> saker = safeStream(connection.hentSaker(aktørId))
                .map(this::tilSak)
                .collect(toList());
        LOG.info("Hentet {} sak(er) {}", saker.size(), saker);
        return saker;
    }

    private List<Behandling> hentBehandlinger(List<Lenke> behandlingsLenker) {
        LOG.info("Henter {} behandling(er)", behandlingsLenker.size());
        List<Behandling> behandlinger = safeStream(behandlingsLenker)
                .map(lenke -> connection.hentBehandling(lenke))
                .map(wrapper -> tilBehandling(wrapper))
                .collect(toList());
        LOG.info("Hentet {} behandling(er) {}", behandlinger.size(), behandlinger);
        return behandlinger;
    }

    private Søknad hentSøknad(Lenke søknadsLenke) {
        Søknad søknad = Optional.ofNullable(connection.hentSøknad(søknadsLenke))
                .map(wrapper -> wrapper.getXml())
                .map(xml -> mapper.tilSøknad(xml))
                .orElse(null);
        LOG.info("Hentet søknad {}}", søknad);
        return søknad;
    }

    private Sak tilSak(SakWrapper wrapper) {
        return new Sak(wrapper.getSaksnummer(), wrapper.getFagsakStatus(), wrapper.getBehandlingTema(),
                wrapper.getAktørId(), wrapper.getAktørIdAnnenPart(), wrapper.getAktørIdBarn(),
                hentBehandlinger(wrapper.getBehandlingsLenker()));
    }

    private Behandling tilBehandling(BehandlingWrapper wrapper) {
        return new Behandling(wrapper.getStatus(), wrapper.getType(), wrapper.getTema(), wrapper.getÅrsak(),
                wrapper.getBehandlendeEnhet(), wrapper.getBehandlendeEnhetNavn(),
                hentSøknad(wrapper.getSøknadsLenke()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + ", connection=" + connection + "]";
    }
}
