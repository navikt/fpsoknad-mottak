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
        LOG.info("Hentet {} sak{}", saker.size(), endelse(saker));
        if (!saker.isEmpty()) {
            LOG.info(CONFIDENTIAL, "{}", saker);
        }
        return saker;
    }

    private List<Behandling> hentBehandlinger(List<Lenke> behandlingsLenker) {
        LOG.info("Henter behandlinger");
        List<Behandling> behandlinger = safeStream(behandlingsLenker)
                .map(lenke -> connection.hentBehandling(lenke))
                .map(this::tilBehandling)
                .collect(toList());
        LOG.info("Hentet {} behandling{}", behandlinger.size(), endelse(behandlinger));
        if (!behandlinger.isEmpty()) {
            LOG.info(CONFIDENTIAL, "{}", behandlinger);
        }
        return behandlinger;
    }

    private InnsynsSøknad hentSøknad(Lenke søknadsLenke) {
        InnsynsSøknad søknad = Optional.ofNullable(connection.hentSøknad(søknadsLenke))
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
        return Optional.ofNullable(wrapper)
                .map(w -> new Sak(w.getSaksnummer(), w.getFagsakStatus(), w.getBehandlingTema(),
                        w.getAktørId(), w.getAktørIdAnnenPart(), w.getAktørIdBarn(),
                        hentBehandlinger(w.getBehandlingsLenker())))
                .orElse(null);
    }

    private Behandling tilBehandling(BehandlingWrapper wrapper) {
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
        return new InnsynsSøknad(mapper.tilSøknad(wrapper.getXml()), wrapper.getJournalpostId());
    }

    private static String endelse(List<?> liste) {
        return liste.size() == 1 ? "" : "er";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + ", connection=" + connection + "]";
    }
}
