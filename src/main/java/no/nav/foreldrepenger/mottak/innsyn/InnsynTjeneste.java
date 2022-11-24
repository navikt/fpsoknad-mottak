package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.common.innsyn.uttaksplan.ArbeidsgiverType.ORGANISASJON;
import static no.nav.foreldrepenger.common.innsyn.uttaksplan.ArbeidsgiverType.PRIVAT;
import static no.nav.foreldrepenger.common.util.StreamUtil.distinctByKey;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.common.util.StringUtil.endelse;
import static no.nav.foreldrepenger.common.util.StringUtil.partialMask;

import java.math.BigDecimal;
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
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.Sak;
import no.nav.foreldrepenger.common.domain.felles.AnnenPart;
import no.nav.foreldrepenger.common.domain.felles.BehandlingTema;
import no.nav.foreldrepenger.common.innsyn.Behandling;
import no.nav.foreldrepenger.common.innsyn.BehandlingResultat;
import no.nav.foreldrepenger.common.innsyn.BehandlingStatus;
import no.nav.foreldrepenger.common.innsyn.BehandlingType;
import no.nav.foreldrepenger.common.innsyn.uttaksplan.ArbeidsgiverInfoDto;
import no.nav.foreldrepenger.common.innsyn.uttaksplan.SøknadsGrunnlagDto;
import no.nav.foreldrepenger.common.innsyn.uttaksplan.UttaksPeriodeDto;
import no.nav.foreldrepenger.common.innsyn.uttaksplan.UttaksplanDto;
import no.nav.foreldrepenger.common.innsyn.v2.AnnenPartVedtak;
import no.nav.foreldrepenger.common.innsyn.v2.FpSak;
import no.nav.foreldrepenger.common.innsyn.v2.PersonDetaljer;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;
import no.nav.foreldrepenger.common.innsyn.v2.Saksnummer;
import no.nav.foreldrepenger.common.innsyn.v2.persondetaljer.Person;
import no.nav.foreldrepenger.mottak.innsyn.dto.BehandlingDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.LenkeDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SakDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.UttaksPeriodeDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.UttaksplanDTO;
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
        var saker = innsyn.sakerV2(aktørId);
        var beriketSaker = berikPerson(saker);
        LOG.info(CONFIDENTIAL, "{}", beriketSaker);
        return beriketSaker;
    }

    @Override
    public Optional<AnnenPartVedtak> annenPartVedtak(AktørId søker,
                                                     AnnenPartVedtakIdentifikator annenPartVedtakIdentifikator) {
        LOG.info("Henter annen parts vedtak");
        var annenPartAktørId = oppslag.aktørId(annenPartVedtakIdentifikator.annenPartFødselsnummer());
        var barnAktørId = oppslag.aktørId(annenPartVedtakIdentifikator.barnFødselsnummer());
        var request = new AnnenPartVedtakRequest(søker, annenPartAktørId, barnAktørId, annenPartVedtakIdentifikator.familiehendelse());
        var vedtak = innsyn.annenPartVedtak(request);
        LOG.info("Returnerer annen parts vedtak. Antall perioder {}", vedtak.map(v -> v.perioder().size()).orElse(0));
        return vedtak;
    }

    private Saker berikPerson(Saker saker) {
        var beriketFpSaker = saker.foreldrepenger().stream()
            .map(this::berikPerson)
            .collect(Collectors.toSet());
        return new Saker(beriketFpSaker, saker.engangsstønad(), saker.svangerskapspenger());
    }

    private FpSak berikPerson(FpSak sak) {
        var søker = oppslag.person();
        return new FpSak(sak.saksnummer(), sak.sakAvsluttet(), sak.sisteSøknadMottattDato(), sak.kanSøkeOmEndring(),
            sak.sakTilhørerMor(), sak.gjelderAdopsjon(), sak.morUføretrygd(), sak.harAnnenForelderTilsvarendeRettEØS(),
            sak.ønskerJustertUttakVedFødsel(), sak.rettighetType(),
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
                    .map(bb -> new Person(new Fødselsnummer(bb.fnr().value()), bb.navn().fornavn(),
                        bb.navn().mellomnavn(), bb.navn().etternavn(), null, bb.fødselsdato()))
                    .orElseThrow(() -> new IllegalArgumentException("Barn med aktørId " + aktørId.value() +  " ikke i resultat fra pdl query basert på knytning til søker"));
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
    public UttaksplanDto uttaksplan(Saksnummer saksnummer) {
        return innsyn.uttaksplan(saksnummer)
            .map(this::tilUttaksplan)
            .orElse(null);
    }

    @Override
    public UttaksplanDto uttaksplan(AktørId aktørId, AktørId annenPart) {
        return innsyn.uttaksplan(aktørId, annenPart)
            .map(this::tilUttaksplan)
            .orElse(null);
    }

    @Override
    public List<Sak> saker(AktørId aktørId) {
        String id = aktørId.value();
        LOG.info("Henter sak(er) for {}", id);
        var saker = safeStream(innsyn.saker(id))
            .filter(distinctByKey(SakDTO::saksnummer))
            .map(this::tilSak)
            .toList();
        LOG.info("Hentet {} sak{}", saker.size(), endelse(saker));
        if (!saker.isEmpty()) {
            LOG.info(CONFIDENTIAL, "{}", saker);
        }
        return saker;
    }

    private List<Behandling> hentBehandlinger(List<LenkeDTO> lenker, String saksnr) {
        LOG.info("Henter {} behandling{} for sak {}", lenker.size(), endelse(lenker), saksnr);
        var behandlinger = safeStream(lenker)
            .filter(distinctByKey(LenkeDTO::href))
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
                w.saksnummer(),
                w.fagsakStatus(),
                w.behandlingTema(),
                w.aktørId(),
                annenPart(w.aktørIdAnnenPart()),
                w.aktørIdBarna(),
                hentBehandlinger(
                    w.behandlingsLenker(),
                    w.saksnummer()),
                w.opprettetTidspunkt(),
                w.endretTidspunkt(),
                w.mottattEndringssøknad()))
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
                .opprettetTidspunkt(w.opprettetTidspunkt())
                .endretTidspunkt(w.endretTidspunkt())
                .behandlendeEnhet(w.behandlendeEnhet())
                .behandlendeEnhetNavn(w.behandlendeEnhetNavn())
                .behandlingResultat(tilResultat(w.behandlingResultat()))
                .status(tilBehandlingStatus(w.status()))
                .tema(tilTema(w.tema()))
                .type(tilType(w.type()))
                .inntektsmeldinger(w.inntektsmeldinger())
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

    private UttaksplanDto tilUttaksplan(UttaksplanDTO dto) {
        var grunnlag = new SøknadsGrunnlagDto(
            dto.termindato(),
            dto.fødselsdato(),
            dto.omsorgsovertakelsesdato(),
            dto.dekningsgrad(),
            dto.antallBarn(),
            dto.søkerErFarEllerMedmor(),
            dto.morErAleneOmOmsorg(),
            dto.morHarRett(),
            dto.morErUfør(),
            dto.harAnnenForelderTilsvarendeRettEØS(),
            dto.farMedmorErAleneOmOmsorg(),
            dto.farMedmorHarRett(),
            dto.annenForelderErInformert(),
            dto.ønskerJustertUttakVedFødsel());
        return new UttaksplanDto(grunnlag, map(dto.uttaksPerioder()));
    }

    private List<UttaksPeriodeDto> map(List<UttaksPeriodeDTO> perioder) {
        return safeStream(perioder)
            .map(this::map)
            .toList();
    }

    private UttaksPeriodeDto map(UttaksPeriodeDTO periode) {
        LOG.trace("Mapper periode {}", periode);
        return Optional.ofNullable(periode)
            .map(p -> new UttaksPeriodeDto(
                p.oppholdAarsak(),
                p.overfoeringAarsak(),
                p.graderingAvslagAarsak(),
                p.utsettelsePeriodeType(),
                p.periodeResultatType(),
                p.graderingInnvilget(),
                p.samtidigUttak(),
                p.fom(),
                p.tom(),
                null, // Settes i konstructør
                p.stønadskontotype(),
                tilDoubleFraBigDecimal(p.trekkDager()),
                p.arbeidstidProsent(),
                p.utbetalingsprosent(),
                p.gjelderAnnenPart(),
                p.morsAktivitet(),
                p.flerbarnsdager(),
                p.manueltBehandlet(),
                p.samtidigUttaksprosent(),
                p.uttakArbeidType(),
                map(periode.arbeidsgiverAktoerId(), periode.arbeidsgiverOrgnr()),
                p.periodeResultatÅrsak()))
            .orElse(null);
    }

    private ArbeidsgiverInfoDto map(AktørId aktørId, Orgnummer orgnr) {
        LOG.trace("Lager arbeidsgiverInfo for  {} {}", aktørId, orgnr);
        return Optional.ofNullable(orgnr)
            .map(o -> new ArbeidsgiverInfoDto(o.value(), organisasjon.navn(o.value()), ORGANISASJON))
            .orElse(new ArbeidsgiverInfoDto(Optional.ofNullable(aktørId).map(AktørId::value).orElse(null),null, PRIVAT));
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
