package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsyn.uttaksplan.ArbeidsgiverType.ORGANISASJON;
import static no.nav.foreldrepenger.mottak.innsyn.uttaksplan.ArbeidsgiverType.PRIVAT;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.distinctByKey;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.StringUtil.endelse;
import static no.nav.foreldrepenger.mottak.util.StringUtil.limit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.domain.felles.AnnenPart;
import no.nav.foreldrepenger.mottak.domain.felles.BehandlingTema;
import no.nav.foreldrepenger.mottak.innsyn.dto.BehandlingDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SakDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SøknadDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.VedtakDTO;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.ArbeidsgiverInfo;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.SøknadsGrunnlag;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.UttaksPeriode;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.Uttaksplan;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.dto.UttaksPeriodeDTO;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.dto.UttaksplanDTO;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.VedtakMetadata;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.XMLVedtakHandler;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@Service
public class InnsynTjeneste implements Innsyn {
    private static final Logger LOG = LoggerFactory.getLogger(InnsynTjeneste.class);
    private final XMLSøknadHandler søknadHandler;
    private final XMLVedtakHandler vedtakHandler;
    private final Oppslag oppslag;
    private final InnsynConnection innsyn;

    public InnsynTjeneste(XMLSøknadHandler søknadHandler, XMLVedtakHandler vedtakHandler,
            InnsynConnection innsyn, Oppslag oppslag) {
        this.innsyn = innsyn;
        this.oppslag = oppslag;
        this.søknadHandler = søknadHandler;
        this.vedtakHandler = vedtakHandler;
    }

    @Override
    public String ping() {
        return innsyn.ping();
    }

    @Override
    public Vedtak vedtak(AktørId aktørId, String saksnummer) {
        return Optional.ofNullable(safeStream(saker(aktørId))
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
    public Uttaksplan uttaksplan(String saksnummer) {
        return Optional.ofNullable(innsyn.uttaksplan(saksnummer))
                .map(this::map)
                .orElse(null);
    }

    @Override
    public Uttaksplan uttaksplan(AktørId aktørId, AktørId annenPart) {
        return Optional.ofNullable(innsyn.uttaksplan(aktørId, annenPart))
                .map(this::map)
                .orElse(null);
    }

    @Override
    public List<Sak> saker(AktørId aktørId) {
        return hentSaker(aktørId.getId());
    }

    @Override
    public List<Sak> hentSaker(String aktørId) {
        LOG.info("Henter sak(er) for {}", aktørId);
        List<Sak> saker = safeStream(innsyn.saker(aktørId))
                .filter(distinctByKey(SakDTO::getSaksnummer))
                .map(this::tilSak)
                .collect(toList());
        LOG.info("Hentet {} sak{}", saker.size(), endelse(saker));
        if (!saker.isEmpty()) {
            LOG.info(CONFIDENTIAL, "{}", saker);
        }
        return saker;
    }

    private List<Behandling> hentBehandlinger(List<Lenke> lenker, String saksnr) {
        LOG.info("Henter {} behandling{} for sak {} fra {}", lenker.size(), endelse(lenker), saksnr, lenker);
        List<Behandling> behandlinger = safeStream(lenker)
                .filter(distinctByKey(Lenke::getHref))
                .map(innsyn::behandling)
                .map(this::tilBehandling)
                .collect(toList());
        LOG.info("Hentet {} behandling{}", behandlinger.size(), endelse(behandlinger));
        if (!behandlinger.isEmpty()) {
            LOG.info(CONFIDENTIAL, "{}", behandlinger);
        }
        return behandlinger;
    }

    private InnsynsSøknad hentSøknad(Lenke lenke) {
        InnsynsSøknad søknad = Optional.ofNullable(innsyn.søknad(lenke))
                .map(this::tilSøknad)
                .orElse(null);
        if (søknad == null) {
            LOG.info("Hentet ingen søknad");
        } else {
            LOG.info("Hentet søknad");
            LOG.info(CONFIDENTIAL, "{}", søknad);
        }
        return søknad;
    }

    private Vedtak hentVedtak(Lenke lenke) {
        Vedtak vedtak = Optional.ofNullable(innsyn.vedtak(lenke))
                .map(this::tilVedtak)
                .orElse(null);
        if (vedtak == null) {
            LOG.info("Hentet intet vedtak");
        } else {
            LOG.info("Hentet vedtak med id {}", vedtak.getFagsakId());
            LOG.info(CONFIDENTIAL, "{}", vedtak.getMetadata());
        }
        return vedtak;
    }

    private Sak tilSak(SakDTO wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper sak fra {}", wrapper);
        Sak sak = Optional.ofNullable(wrapper)
                .map(w -> new Sak(
                        w.getSaksnummer(),
                        w.getFagsakStatus(),
                        w.getBehandlingTema(),
                        w.getAktørId(),
                        annenPart(w.getAktørIdAnnenPart()),
                        w.getAktørIdBarna(),
                        hentBehandlinger(
                                w.getBehandlingsLenker(),
                                w.getSaksnummer()),
                        w.getOpprettetTidspunkt(),
                        w.getEndretTidspunkt()))
                .orElse(null);
        LOG.trace(CONFIDENTIAL, "Mappet til sak {}", sak);
        return sak;
    }

    private AnnenPart annenPart(String aktørId) {
        if (aktørId != null) {
            LOG.trace(CONFIDENTIAL, "Henter annen part fnr fra {}", aktørId);
            Fødselsnummer fnr = fnr(aktørId);
            LOG.trace(CONFIDENTIAL, "Fikk {}", fnr);
            return new AnnenPart(fnr, new AktørId(aktørId), navnFor(fnr));
        }
        return null;
    }

    private Navn navnFor(Fødselsnummer fnr) {
        return Optional.ofNullable(fnr)
                .map(Fødselsnummer::getFnr)
                .map(this::navnFor)
                .orElse(null);
    }

    private Navn navnFor(String fnr) {
        try {
            LOG.trace(CONFIDENTIAL, "Henter annen part navn fra {}", fnr);
            Navn navn = oppslag.hentNavn(fnr);
            LOG.trace(CONFIDENTIAL, "Fikk navn {}", navn);
            return navn;
        } catch (Exception e) {
            LOG.warn("Kunne ikke slå opp navn for annen part for {}", fnr);
            return null;
        }
    }

    private Fødselsnummer fnr(String aktørId) {
        try {
            return Optional.ofNullable(aktørId)
                    .map(AktørId::new)
                    .map(oppslag::getFnr)
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
                        .behandlingResultat(tilResultat(w.getBehandlingResultat()))
                        .status(tilBehandlingStatus(w.getStatus()))
                        .årsak(tilÅrsak(w.getÅrsak()))
                        .tema(tilTema(w.getTema()))
                        .type(tilType(w.getType()))
                        .inntektsmeldinger(w.getInntektsmeldinger())
                        .søknad(hentSøknad(w.getSøknadsLenke()))
                        //.vedtak(hentVedtak(w.getVedtaksLenke()))
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
            LOG.trace("Feil ved mapping av søknad fra {}", limit(xml, 50), e);
            return null;
        }
    }

    private Vedtak tilVedtak(VedtakDTO wrapper) {
        String xml = wrapper.getXml();
        try {
            SøknadEgenskap e = vedtakHandler.inspiser(xml);
            LOG.trace(CONFIDENTIAL, "Mapper vedtak av type {} fra {}", e, wrapper);
            return Optional.ofNullable(vedtakHandler.tilVedtak(xml, e))
                    .map(v -> v.withMetadata(new VedtakMetadata(wrapper.getJournalpostId(), e)))
                    .orElse(null);
        } catch (Exception e) {
            LOG.trace("Feil ved mapping av vedtak fra {}", limit(xml, 50), e);
            return null;
        }
    }

    private static BehandlingTema tilTema(String tema) {
        return Optional.ofNullable(tema)
                .map(BehandlingTema::valueSafelyOf)
                .orElse(null);
    }

    private static BehandlingÅrsak tilÅrsak(String årsak) {
        return Optional.ofNullable(årsak)
                .map(BehandlingÅrsak::valueSafelyOf)
                .orElse(null);
    }

    private static BehandlingResultat tilResultat(String resultat) {
        return Optional.ofNullable(resultat)
                .map(BehandlingResultat::valueSafelyOf)
                .orElse(null);
    }

    private static BehandlingType tilType(String type) {
        return Optional.ofNullable(type)
                .map(BehandlingType::valueSafelyOf)
                .orElse(null);
    }

    private static BehandlingStatus tilBehandlingStatus(String status) {
        return Optional.ofNullable(status)
                .map(BehandlingStatus::valueSafelyOf)
                .orElse(null);
    }

    private Uttaksplan map(UttaksplanDTO dto) {
        SøknadsGrunnlag grunnlag = new SøknadsGrunnlag(dto.getTermindato(), dto.getFødselsdato(),
                dto.getOmsorgsovertakelsesdato(),
                dto.getDekningsgrad(), dto.getAntallBarn(), dto.getSøkerErFarEllerMedmor(), dto.getMorErAleneOmOmsorg(),
                dto.getMorHarRett(), dto.getMorErUfør(), dto.getFarMedmorErAleneOmOmsorg(), dto.getFarMedmorHarRett(),
                dto.getAnnenForelderErInformert());
        return new Uttaksplan(grunnlag, map(dto.getUttaksPerioder()));
    }

    private List<UttaksPeriode> map(List<UttaksPeriodeDTO> perioder) {
        return safeStream(perioder)
                .map(this::map)
                .collect(toList());
    }

    private UttaksPeriode map(UttaksPeriodeDTO periode) {
        LOG.trace("Mapper periode {}", periode);
        return Optional.ofNullable(periode)
                .map(p -> new UttaksPeriode(p.getOppholdAarsak(), p.getOverfoeringAarsak(),
                        p.getGraderingAvslagAarsak(),
                        p.getUtsettelsePeriodeType(),
                        p.getPeriodeResultatType(),
                        p.getGraderingInnvilget(),
                        p.getSamtidigUttak(),
                        p.getFom(),
                        p.getTom(),
                        p.getStønadskontotype(),
                        tilDoubleFraBigDecimal(p.getTrekkDager()),
                        p.getArbeidstidProsent(),
                        p.getUtbetalingsprosent(),
                        p.getGjelderAnnenPart(),
                        p.getManueltBehandlet(),
                        p.getSamtidigUttaksprosent(),
                        p.getMorsAktivitet(),
                        p.getFlerbarnsdager(),
                        p.getUttakArbeidType(),
                        map(periode.getArbeidsgiverAktoerId(), periode.getArbeidsgiverOrgnr()),
                        p.getPeriodeResultatÅrsak()))
                .orElse(null);
    }

    private ArbeidsgiverInfo map(AktørId aktørId, String orgnr) {
        LOG.trace("Lager arbeidsgiverInfo for  {} {}", aktørId, orgnr);
        return Optional.ofNullable(orgnr)
                .map(o -> new ArbeidsgiverInfo(o, ORGANISASJON, oppslag.organisasjonsNavn(o)))
                .orElse(new ArbeidsgiverInfo(Optional.ofNullable(aktørId).map(AktørId::getId).orElse(null), PRIVAT,
                        null));
    }

    public static Double tilDoubleFraBigDecimal(BigDecimal value) {
        return Optional.ofNullable(value)
                .map(BigDecimal::doubleValue)
                .orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[søknadHandler=" + søknadHandler + ", vedtakHandler=" + vedtakHandler
                + ", oppslag=" + oppslag + ", innsyn=" + innsyn + "]";
    }

}
