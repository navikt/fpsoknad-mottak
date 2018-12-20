package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsyn.XMLMapper.VERSJONSBEVISST;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.innsyn.dto.BehandlingDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SakDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SøknadDTO;
import no.nav.foreldrepenger.mottak.util.SøknadInspektør;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Service
public class InnsynTjeneste implements Innsyn {

    private static final Logger LOG = LoggerFactory.getLogger(InnsynTjeneste.class);

    private final XMLMapper mapper;
    private final InnsynConnection innsynConnection;
    private final SøknadInspektør inspektør;

    public InnsynTjeneste(InnsynConnection innsynConnection, @Qualifier(VERSJONSBEVISST) XMLMapper mapper,
            SøknadInspektør inspektør) {
        this.innsynConnection = innsynConnection;
        this.mapper = mapper;
        this.inspektør = inspektør;
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

    private Sak tilSak(SakDTO wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper sak fra {}", wrapper);
        return Optional.ofNullable(wrapper)
                .map(w -> new Sak(w.getSaksnummer(), w.getFagsakStatus(), w.getBehandlingTema(),
                        w.getAktørId(), w.getAktørIdAnnenPart(), w.getAktørIdBarna(),
                        hentBehandlinger(w.getBehandlingsLenker()), w.getOpprettetTidspunkt(), w.getEndretTidspunkt()))
                .orElse(null);
    }

    private Behandling tilBehandling(BehandlingDTO wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper behandling fra {}", wrapper);
        return Optional.ofNullable(wrapper)
                .map(w -> new Behandling.BehandlingBuilder()
                        .opprettetTidspunkt(wrapper.getOpprettetTidspunkt())
                        .endretTidspunkt(wrapper.getEndretTidspunkt())
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

    private InnsynsSøknad tilSøknad(SøknadDTO wrapper) {
        Versjon versjon = inspektør.versjon(wrapper.getXml());
        if (inspektør.erEngangsstønad(wrapper.getXml())) {
            LOG.info("Dette er en engangsstønad, mappes ikke foreløpig");
            return new InnsynsSøknad(versjon, null, wrapper.getJournalpostId());
        }
        LOG.trace(CONFIDENTIAL, "Mapper søknad versjon {} fra {}", versjon.name(), wrapper);
        return new InnsynsSøknad(versjon, mapper.tilSøknad(wrapper.getXml()),
                wrapper.getJournalpostId());
    }

    private static String endelse(List<?> liste) {
        return liste.size() == 1 ? "" : "er";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + ", innsynConnection=" + innsynConnection
                + ", inspektør=" + inspektør + "]";
    }

}
