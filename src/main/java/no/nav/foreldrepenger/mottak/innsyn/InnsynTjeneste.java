package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Barn;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsyn.AnnenPartVedtak;
import no.nav.foreldrepenger.common.innsyn.FpSak;
import no.nav.foreldrepenger.common.innsyn.PersonDetaljer;
import no.nav.foreldrepenger.common.innsyn.Saker;
import no.nav.foreldrepenger.common.innsyn.persondetaljer.Person;
import no.nav.foreldrepenger.mottak.oppslag.OppslagTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.common.util.StringUtil.partialMask;

@Service
public class InnsynTjeneste implements Innsyn {
    private static final Logger LOG = LoggerFactory.getLogger(InnsynTjeneste.class);
    private final OppslagTjeneste oppslag;
    private final InnsynConnection innsyn;

    public InnsynTjeneste(InnsynConnection innsyn, OppslagTjeneste oppslag) {
        this.innsyn = innsyn;
        this.oppslag = oppslag;
    }

    @Override
    public String ping() {
        return innsyn.ping();
    }

    @Override
    public Saker saker(AktørId aktørId) {
        LOG.info("Henter saker for aktørId {}", partialMask(aktørId.value(), 6));
        var saker = innsyn.saker(aktørId);
        var beriketSaker = berikPerson(saker);
        LOG.info(CONFIDENTIAL, "{}", beriketSaker);
        return beriketSaker;
    }

    @Override
    public Optional<AnnenPartVedtak> annenPartVedtak(AktørId søker,
                                                     AnnenPartVedtakIdentifikator annenPartVedtakIdentifikator) {
        if (annenPartVedtakIdentifikator.annenPartFødselsnummer() == null || annenPartVedtakIdentifikator.annenPartFødselsnummer().value().isBlank()) {
            LOG.warn("Feil input annen parts fnr {}. Returnerer tomt resultat", annenPartVedtakIdentifikator.annenPartFødselsnummer());
            return Optional.empty();
        }
        LOG.info("Henter annen parts vedtak");
        AktørId annenPartAktørId;
        try {
            annenPartAktørId = oppslag.aktørId(annenPartVedtakIdentifikator.annenPartFødselsnummer());
        } catch (Exception e) {
            LOG.warn("Feil ved mapping fra fnr til aktørid for annen part. Returnerer tomt resultat", e);
            return Optional.empty();
        }
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
        var søker = oppslag.personMedAlleBarn();
        return new FpSak(sak.saksnummer(), sak.sakAvsluttet(), sak.sisteSøknadMottattDato(), sak.kanSøkeOmEndring(),
            sak.sakTilhørerMor(), sak.gjelderAdopsjon(), sak.morUføretrygd(), sak.harAnnenForelderTilsvarendeRettEØS(),
            sak.ønskerJustertUttakVedFødsel(), sak.rettighetType(),
            berik(sak.annenPart()), sak.familiehendelse(), sak.gjeldendeVedtak(), sak.åpenBehandling(),
            barn(sak.barn(), søker.barn()), sak.dekningsgrad());
    }

    private Set<PersonDetaljer> barn(Set<PersonDetaljer> barn, List<Barn> søkerBarn) {
        return barn.stream()
            .map(b -> {
                var aktørId = (no.nav.foreldrepenger.common.innsyn.persondetaljer.AktørId) b;
                var fødselsnummer = oppslag.fnr(new AktørId(aktørId.value()));
                return søkerBarn.stream()
                    .filter(sb -> Objects.equals(sb.fnr(), fødselsnummer))
                    .findFirst()
                    .map(bb -> new Person(new Fødselsnummer(bb.fnr().value()), bb.navn().fornavn(),
                        bb.navn().mellomnavn(), bb.navn().etternavn(), null, bb.fødselsdato()))
                    //Null her kan være adressebeskyttet barn, de er filtrert ut  i søkerBarn
                    .orElse(null);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    private no.nav.foreldrepenger.common.innsyn.AnnenPart berik(no.nav.foreldrepenger.common.innsyn.AnnenPart annenPart) {
        if (annenPart == null) {
            return null;
        }
        var aktørId = (no.nav.foreldrepenger.common.innsyn.persondetaljer.AktørId) annenPart.personDetaljer();
        var fødselsnummer = oppslag.fnr(new AktørId(aktørId.value()));
        var navnOpt = oppslag.annenPartNavn(fødselsnummer.value());
        if (navnOpt.isEmpty()) {
            //Har fnr, men finner ikke navn. Mulig adressebeskyttelse
            return null;
        }
        var navn = navnOpt.get();
        var person = new Person(new Fødselsnummer(fødselsnummer.value()),
            navn.fornavn(), navn.mellomnavn(), navn.etternavn(), null, null);
        return new no.nav.foreldrepenger.common.innsyn.AnnenPart(person);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[oppslag=" + oppslag + ", innsyn=" + innsyn + "]";
    }

}
