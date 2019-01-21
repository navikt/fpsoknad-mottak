package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsyn.XMLMapper.VERSJONSBEVISST;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.StringUtil.endelse;

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

    private List<Behandling> hentBehandlinger(List<Lenke> behandlingsLenker, String saksnr) {
        LOG.info("Henter {} behandling{} for sak {} fra {}", behandlingsLenker.size(), endelse(behandlingsLenker),
                saksnr, behandlingsLenker);
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
        LOG.info("Henter søknad fra {}", søknadsLenke);
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
                .map(w -> new Sak(
                        w.getSaksnummer(),
                        w.getFagsakStatus(),
                        w.getBehandlingTema(),
                        w.getAktørId(),
                        w.getAktørIdAnnenPart(),
                        w.getAktørIdBarna(),
                        hentBehandlinger(w.getBehandlingsLenker(), w.getSaksnummer()), w.getOpprettetTidspunkt(),
                        w.getEndretTidspunkt()))
                .orElse(null);
    }

    private Behandling tilBehandling(BehandlingDTO wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper behandling fra {}", wrapper);
        return Optional.ofNullable(wrapper)
                .map(w -> new Behandling.BehandlingBuilder()
                        .opprettetTidspunkt(w.getOpprettetTidspunkt())
                        .endretTidspunkt(w.getEndretTidspunkt())
                        .behandlendeEnhet(w.getBehandlendeEnhet())
                        .behandlendeEnhetNavn(w.getBehandlendeEnhetNavn())
                        .status(w.getStatus())
                        .årsak(w.getÅrsak())
                        .tema(w.getTema())
                        .type(w.getType())
                        .inntektsmeldinger(w.getInntektsmeldinger())
                        .søknad(hentSøknad(w.getSøknadsLenke()))
                        .build())
                .orElse(null);
    }

    private InnsynsSøknad tilSøknad(SøknadDTO wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper søknad fra {}", wrapper);
        String xml = wrapper.getXml();
        return new InnsynsSøknad(new SøknadMetadata(inspektør.inspiser(xml), wrapper.getJournalpostId()),
                mapper.tilSøknad(xml));

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + ", innsynConnection=" + innsynConnection
                + ", inspektør=" + inspektør + "]";
    }

}
