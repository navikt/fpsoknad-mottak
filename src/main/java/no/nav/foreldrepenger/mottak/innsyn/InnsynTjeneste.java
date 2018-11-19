package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.XMLTilSøknadMapper;

@Service
public class InnsynTjeneste implements Innsyn {

    private static final Logger LOG = LoggerFactory.getLogger(InnsynTjeneste.class);

    private final XMLTilSøknadMapper mapper;
    private final InnsynConnection innsynConnection;

    public InnsynTjeneste(InnsynConnection innsynConnection, XMLTilSøknadMapper mapper) {
        this.innsynConnection = innsynConnection;
        this.mapper = mapper;
    }

    @Override
    public List<UttaksPeriode> hentUttaksplan(String saksnummer) {
        return innsynConnection.hentUttaksplan(saksnummer);
    }

    @Override
    public List<Sak> hentSaker(AktorId aktørId) {
        return hentSaker(aktørId.getId());
    }

    @Override
    public List<Sak> hentSaker(String aktørId) {
        LOG.info("Henter sak(er) for {}", aktørId);
        List<Sak> saker = safeStream(innsynConnection.hentSaker(aktørId))
                .map(this::tilSak)
                .collect(toList());
        LOG.info("Hentet {} sak{}", saker.size(), endelse(saker));
        if (!saker.isEmpty()) {
            LOG.info(CONFIDENTIAL, "{}", saker);
        }
        return saker;
    }

    private List<Behandling> hentBehandlinger(List<Lenke> behandlingsLenker) {
        LOG.info("Henter {} behandlinger", behandlingsLenker.size());
        List<Behandling> behandlinger = safeStream(behandlingsLenker)
                .map(innsynConnection::hentBehandling)
                .map(this::tilBehandling)
                .collect(toList());
        LOG.info("Hentet {} behandling{}", behandlinger.size(), endelse(behandlinger));
        if (!behandlinger.isEmpty()) {
            LOG.info(CONFIDENTIAL, "{}", behandlinger);
        }
        return behandlinger;
    }

    private InnsynsSøknad hentSøknad(Lenke søknadsLenke) {
        LOG.info("Henter søknad");
        InnsynsSøknad søknad = Optional.ofNullable(innsynConnection.hentSøknad(søknadsLenke))
                .map(this::tilSøknad)
                .orElse(null);
        if (søknad == null) {
            LOG.info("Hentet ingen søknad");
        }
        else {
            LOG.info("Hentet søknad");
            LOG.info(CONFIDENTIAL, "{}", søknad);
        }
        return søknad;
    }

    private Sak tilSak(SakWrapper wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper sak fra {}", wrapper);
        return Optional.ofNullable(wrapper)
                .map(w -> new Sak(w.getSaksnummer(), w.getFagsakStatus(), w.getBehandlingTema(),
                        w.getAktørId(), w.getAktørIdAnnenPart(), w.getAktørIdBarna(),
                        hentBehandlinger(w.getBehandlingsLenker()), w.getOpprettetTidspunkt(), w.getEndretTidspunkt()))
                .orElse(null);
    }

    private Behandling tilBehandling(BehandlingWrapper wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper behandling fra {}", wrapper);
        return Optional.ofNullable(wrapper)
                .map(w -> new Behandling.BehandlingBuilder()
                        .behandlendeEnhet(wrapper.getBehandlendeEnhet())
                        .behandlendeEnhetNavn(wrapper.getBehandlendeEnhetNavn())
                        .status(wrapper.getStatus())
                        .årsak(wrapper.getÅrsak())
                        .tema(wrapper.getTema())
                        .type(wrapper.getType())
                        .søknad(hentSøknad(wrapper.getSøknadsLenke()))
                        .build())
                .orElse(null);
    }

    private InnsynsSøknad tilSøknad(SøknadWrapper wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper søknad fra {}", wrapper);
        return new InnsynsSøknad(mapper.tilSøknad(wrapper.getXml()), wrapper.getJournalpostId());
    }

    private static String endelse(List<?> liste) {
        return liste.size() == 1 ? "" : "er";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + ", innsynConnection=" + innsynConnection + "]";
    }

}
