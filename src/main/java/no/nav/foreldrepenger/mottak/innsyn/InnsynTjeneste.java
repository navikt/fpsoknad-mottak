package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.distinctByKey;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.StringUtil.endelse;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.domain.felles.BehandlingsTema;
import no.nav.foreldrepenger.mottak.innsyn.dto.BehandlingDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SakDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SøknadDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.VedtakDTO;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.Uttaksplan;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.VedtakMetadata;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.XMLVedtakHandler;
import no.nav.foreldrepenger.mottak.oppslag.OppslagConnection;

@Service
public class InnsynTjeneste implements Innsyn {

    private static final Logger LOG = LoggerFactory.getLogger(InnsynTjeneste.class);

    private final XMLSøknadHandler søknadHandler;
    private final XMLVedtakHandler vedtakHandler;
    private final OppslagConnection oppslagConnection;
    private final InnsynConnection innsynConnection;

    public InnsynTjeneste(XMLSøknadHandler søknadHandler, XMLVedtakHandler vedtakHandler,
            InnsynConnection innsynConnection, OppslagConnection oppslagConnection) {
        this.innsynConnection = innsynConnection;
        this.oppslagConnection = oppslagConnection;
        this.søknadHandler = søknadHandler;
        this.vedtakHandler = vedtakHandler;
    }

    @Override
    public String ping() {
        return innsynConnection.ping();
    }

    @Override
    public Vedtak hentVedtak(AktorId aktørId, String saksnummer) {
        return Optional.ofNullable(safeStream(hentSaker(aktørId))
                .filter(s -> s.getSaksnummer().equals(saksnummer))
                .findFirst()
                .orElse(null))
                .map(Sak::getBehandlinger)
                .orElse(emptyList())
                .stream()
                .sorted(comparing(Behandling::getEndretTidspunkt)
                        .reversed())
                .collect(toList())
                .stream()
                .findFirst()
                .map(Behandling::getVedtak)
                .orElse(null);
    }

    @Override
    public Uttaksplan hentUttaksplan(String saksnummer) {
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
                .filter(distinctByKey(SakDTO::getSaksnummer))
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
                .filter(distinctByKey(Lenke::getHref))
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

    private Vedtak hentVedtak(Lenke vedtaksLenke) {
        Vedtak vedtak = Optional.ofNullable(innsynConnection.hentVedtak(vedtaksLenke))
                .map(this::tilVedtak)
                .orElse(null);
        if (vedtak == null) {
            LOG.info("Hentet intet vedtak");
        }
        else {
            LOG.info("Hentet vedtak med id {}", vedtak.getFagsakId());
            LOG.info(CONFIDENTIAL, "{}", vedtak);
        }
        return vedtak;
    }

    private Sak tilSak(SakDTO wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper sak fra {}", wrapper);
        return Optional.ofNullable(wrapper)
                .map(w -> new Sak(
                        w.getSaksnummer(),
                        w.getFagsakStatus(),
                        w.getBehandlingTema(),
                        w.getAktørId(),
                        fnrForAktørId(w.getAktørIdAnnenPart()),
                        w.getAktørIdBarna(),
                        hentBehandlinger(
                                w.getBehandlingsLenker(),
                                w.getSaksnummer()),
                        w.getOpprettetTidspunkt(),
                        w.getEndretTidspunkt()))
                .orElse(null);
    }

    private Fødselsnummer fnrForAktørId(String aktørId) {
        try {
            return Optional.ofNullable(aktørId)
                    .map(AktorId::new)
                    .map(oppslagConnection::getFnr)
                    .orElse(null);
        } catch (Exception e) {
            LOG.warn("Kunne ikke slå opp FNR for annen part for aktørid {}", aktørId);
            return null;
        }
    }

    private Behandling tilBehandling(BehandlingDTO wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper behandling fra {}", wrapper);
        return Optional.ofNullable(wrapper)
                .map(w -> new Behandling.BehandlingBuilder()
                        .opprettetTidspunkt(w.getOpprettetTidspunkt())
                        .endretTidspunkt(w.getEndretTidspunkt())
                        .behandlendeEnhet(w.getBehandlendeEnhet())
                        .behandlendeEnhetNavn(w.getBehandlendeEnhetNavn())
                        .behandlingResultat(w.getBehandlingResultat())
                        .status(w.getStatus())
                        .årsak(w.getÅrsak())
                        .tema(tilTema(w.getTema()))
                        .type(w.getType())
                        .inntektsmeldinger(w.getInntektsmeldinger())
                        .søknad(hentSøknad(w.getSøknadsLenke()))
                        .vedtak(hentVedtak(w.getVedtaksLenke()))
                        .build())
                .orElse(null);
    }

    private InnsynsSøknad tilSøknad(SøknadDTO wrapper) {
        String xml = wrapper.getXml();
        try {
            LOG.trace(CONFIDENTIAL, "Mapper søknad fra {}", wrapper);
            SøknadEgenskap egenskaper = søknadHandler.inspiser(xml);
            return new InnsynsSøknad(new SøknadMetadata(egenskaper, wrapper.getJournalpostId()),
                    søknadHandler.tilSøknad(xml, egenskaper));
        } catch (Exception e) {
            LOG.warn("Feil ved mapping av søknad fra {}", xml, e);
            return null;
        }
    }

    private Vedtak tilVedtak(VedtakDTO wrapper) {
        String xml = wrapper.getXml();
        try {
            LOG.trace(CONFIDENTIAL, "Mapper vedtak fra {}", wrapper);
            SøknadEgenskap e = vedtakHandler.inspiser(xml);
            return vedtakHandler.tilVedtak(xml, e)
                    .withMetadata(new VedtakMetadata(wrapper.getJournalpostId(), e));
        } catch (Exception e) {
            LOG.warn("Feil ved mapping av vedtak fra {}", xml, e);
            return null;
        }
    }

    private static BehandlingsTema tilTema(String tema) {
        return Optional.ofNullable(tema)
                .map(BehandlingsTema::valueOf)
                .orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [søknadHandler=" + søknadHandler + ", vedtakHandler=" + vedtakHandler
                + ", innsynConnection=" + innsynConnection + "]";
    }

}
