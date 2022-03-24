package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.common.util.StreamUtil.distinctByKey;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.common.util.StringUtil.endelse;
import static no.nav.foreldrepenger.common.util.StringUtil.partialMask;
import static no.nav.foreldrepenger.mottak.innsyn.uttaksplan.ArbeidsgiverType.ORGANISASJON;
import static no.nav.foreldrepenger.mottak.innsyn.uttaksplan.ArbeidsgiverType.PRIVAT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Barn;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Sak;
import no.nav.foreldrepenger.common.domain.felles.AnnenPart;
import no.nav.foreldrepenger.common.domain.felles.BehandlingTema;
import no.nav.foreldrepenger.common.innsyn.Behandling;
import no.nav.foreldrepenger.common.innsyn.BehandlingResultat;
import no.nav.foreldrepenger.common.innsyn.BehandlingStatus;
import no.nav.foreldrepenger.common.innsyn.BehandlingType;
import no.nav.foreldrepenger.common.innsyn.v2.FpSak;
import no.nav.foreldrepenger.common.innsyn.v2.PersonDetaljer;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;
import no.nav.foreldrepenger.common.innsyn.v2.persondetaljer.Kjønn;
import no.nav.foreldrepenger.common.innsyn.v2.persondetaljer.Person;
import no.nav.foreldrepenger.mottak.innsyn.dto.BehandlingDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SakDTO;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.ArbeidsgiverInfo;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.SøknadsGrunnlag;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.UttaksPeriode;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.Uttaksplan;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.dto.UttaksPeriodeDTO;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.dto.UttaksplanDTO;
import no.nav.foreldrepenger.mottak.oppslag.OppslagTjeneste;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.OrganisasjonConnection;

@Service
public class InnsynTjeneste implements Innsyn {
    private static final Logger LOG = LoggerFactory.getLogger(InnsynTjeneste.class);
    private final OppslagTjeneste oppslag;
    private final OrganisasjonConnection organisasjon;
    private final InnsynConnection innsyn;

    public InnsynTjeneste(InnsynConnection innsyn, OppslagTjeneste oppslag, OrganisasjonConnection organisasjon) {
        this.innsyn = innsyn;
        this.oppslag = oppslag;
        this.organisasjon = organisasjon;
    }

    @Override
    public String ping() {
        return innsyn.ping();
    }

    @Override
    public Saker sakerV2(AktørId aktørId) {
        LOG.info("Henter sakerV2 for aktørId {}", partialMask(aktørId.value(), 13));
        var saker = innsyn.sakerV2(aktørId.value());
        var beriketSaker = berikPerson(saker);
        LOG.info(CONFIDENTIAL, "{}", beriketSaker);
        return beriketSaker;
    }

    private Saker berikPerson(Saker saker) {
        var beriketFpSaker = saker.foreldrepenger().stream()
            .map(this::berikPerson)
            .collect(Collectors.toSet());
        return new Saker(beriketFpSaker, saker.engangsstønad(), saker.svangerskapspenger());
    }

    private FpSak berikPerson(FpSak sak) {
        var søker = oppslag.person();
        return new FpSak(sak.saksnummer(), sak.sakAvsluttet(), sak.kanSøkeOmEndring(),
            sak.sakTilhørerMor(), sak.gjelderAdopsjon(), sak.morUføretrygd(), sak.rettighetType(),
            berik(sak.annenPart()), sak.familiehendelse(), sak.gjeldendeVedtak(), sak.åpenBehandling(),
            barn(sak.barn(), søker.barn()), sak.dekningsgrad());
    }

    private Set<PersonDetaljer> barn(Set<PersonDetaljer> barn, Set<Barn> søkerBarn) {
        return barn.stream()
            .map(b -> {
                var aktørId = (no.nav.foreldrepenger.common.innsyn.v2.persondetaljer.AktørId) b;
                var fødselsnummer = oppslag.fnr(new AktørId(aktørId.value()));
                return søkerBarn.stream()
                    .filter(sb -> sb.fnr().equals(fødselsnummer))
                    .findFirst()
                    .map(bb -> new Person(new Fødselsnummer(bb.fnr().value()),
                            bb.navn().fornavn(), bb.navn().mellomnavn(), bb.navn().etternavn(), Kjønn.U, bb.fødselsdato()))
                    .orElseGet(() -> {
                        LOG.warn("Barn med aktørId {} ikke i resultat fra pdl query basert på knytning til søker", aktørId.value());
                        return new Person(new Fødselsnummer(fødselsnummer.value()),
                            "Barn", "", "Barnesen", Kjønn.U, LocalDate.now());
                    });
            })
            .collect(Collectors.toSet());
    }

    private no.nav.foreldrepenger.common.innsyn.v2.AnnenPart berik(no.nav.foreldrepenger.common.innsyn.v2.AnnenPart annenPart) {
        if (annenPart == null) {
            return null;
        }
        var aktørId = (no.nav.foreldrepenger.common.innsyn.v2.persondetaljer.AktørId) annenPart.personDetaljer();
        var fødselsnummer = oppslag.fnr(new AktørId(aktørId.value()));
        var navn = oppslag.navn(fødselsnummer.value());
        var person = new Person(new Fødselsnummer(fødselsnummer.value()),
            navn.fornavn(), navn.mellomnavn(), navn.etternavn(), null, null);
        return new no.nav.foreldrepenger.common.innsyn.v2.AnnenPart(person);
    }

    @Override
    public Uttaksplan uttaksplan(String saksnummer) {
        return Optional.ofNullable(innsyn.uttaksplan(saksnummer))
                .map(this::tilUttaksplan)
                .orElse(null);
    }

    @Override
    public Uttaksplan uttaksplan(AktørId aktørId, AktørId annenPart) {
        return Optional.ofNullable(innsyn.uttaksplan(aktørId, annenPart))
                .map(this::tilUttaksplan)
                .orElse(null);
    }

    @Override
    public List<Sak> saker(AktørId aktørId) {
        String id = aktørId.value();
        LOG.info("Henter sak(er) for {}", id);
        var saker = safeStream(innsyn.saker(id))
                .filter(distinctByKey(SakDTO::getSaksnummer))
                .map(this::tilSak)
                .toList();
        LOG.info("Hentet {} sak{}", saker.size(), endelse(saker));
        if (!saker.isEmpty()) {
            LOG.info(CONFIDENTIAL, "{}", saker);
        }
        return saker;
    }

    private List<Behandling> hentBehandlinger(List<Lenke> lenker, String saksnr) {
        LOG.info("Henter {} behandling{} for sak {}", lenker.size(), endelse(lenker), saksnr);
        var behandlinger = safeStream(lenker)
                .filter(distinctByKey(Lenke::href))
                .map(innsyn::behandling)
                .map(this::tilBehandling)
                .toList();
        LOG.info("Hentet {} behandling{} for sak {}", behandlinger.size(), endelse(behandlinger), saksnr);
        if (!behandlinger.isEmpty()) {
            LOG.info(CONFIDENTIAL, "{}", behandlinger);
        }
        return behandlinger;
    }

    private Sak tilSak(SakDTO wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper sak fra {}", wrapper);
        var sak = Optional.ofNullable(wrapper)
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
                        w.getEndretTidspunkt(),
                        w.isMottattEndringssøknad()))
                .orElse(null);
        LOG.trace(CONFIDENTIAL, "Mappet til sak {}", sak);
        return sak;
    }

    private AnnenPart annenPart(AktørId aktørId) {
        if (aktørId != null) {
            LOG.trace(CONFIDENTIAL, "Henter annen part fnr fra {}", aktørId);
            Fødselsnummer fnr = oppslag.fnr(aktørId);
            LOG.trace(CONFIDENTIAL, "Fikk {}", fnr);
            return new AnnenPart(fnr, aktørId, oppslag.navn(fnr.value()), null);
        }
        return null;
    }

    private Behandling tilBehandling(BehandlingDTO wrapper) {
        LOG.trace(CONFIDENTIAL, "Mapper behandling fra {}", wrapper);
        return Optional.ofNullable(wrapper)
                .map(w -> Behandling.builder()
                        .opprettetTidspunkt(w.getOpprettetTidspunkt())
                        .endretTidspunkt(w.getEndretTidspunkt())
                        .behandlendeEnhet(w.getBehandlendeEnhet())
                        .behandlendeEnhetNavn(w.getBehandlendeEnhetNavn())
                        .behandlingResultat(tilResultat(w.getBehandlingResultat()))
                        .status(tilBehandlingStatus(w.getStatus()))
                        .tema(tilTema(w.getTema()))
                        .type(tilType(w.getType()))
                        .inntektsmeldinger(w.getInntektsmeldinger())
                        .build())
                .orElse(null);
    }

    private static BehandlingTema tilTema(String tema) {
        return Optional.ofNullable(tema)
                .map(BehandlingTema::valueSafelyOf)
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

    private Uttaksplan tilUttaksplan(UttaksplanDTO dto) {
        var grunnlag = new SøknadsGrunnlag(dto.getTermindato(), dto.getFødselsdato(),
                dto.getOmsorgsovertakelsesdato(),
                dto.getDekningsgrad(), dto.getAntallBarn(), dto.getSøkerErFarEllerMedmor(), dto.getMorErAleneOmOmsorg(),
                dto.getMorHarRett(), dto.getMorErUfør(), dto.getFarMedmorErAleneOmOmsorg(), dto.getFarMedmorHarRett(),
                dto.getAnnenForelderErInformert());
        return new Uttaksplan(grunnlag, map(dto.getUttaksPerioder()));
    }

    private List<UttaksPeriode> map(List<UttaksPeriodeDTO> perioder) {
        return safeStream(perioder)
                .map(this::map)
                .toList();
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
                .map(o -> new ArbeidsgiverInfo(o, ORGANISASJON, organisasjon.navn(o)))
                .orElse(new ArbeidsgiverInfo(Optional.ofNullable(aktørId)
                        .map(AktørId::value)
                        .orElse(null), PRIVAT,
                        null));
    }

    private static Double tilDoubleFraBigDecimal(BigDecimal value) {
        return Optional.ofNullable(value)
                .map(BigDecimal::doubleValue)
                .orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[oppslag=" + oppslag + ", innsyn=" + innsyn + "]";
    }

}
