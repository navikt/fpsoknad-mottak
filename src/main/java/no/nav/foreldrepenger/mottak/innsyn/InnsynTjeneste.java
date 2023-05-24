package no.nav.foreldrepenger.mottak.innsyn;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Barn;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsyn.AnnenPartVedtak;
import no.nav.foreldrepenger.common.innsyn.FpSak;
import no.nav.foreldrepenger.common.innsyn.Person;
import no.nav.foreldrepenger.common.innsyn.Saker;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;

@Service
public class InnsynTjeneste implements Innsyn {
    private static final Logger LOG = LoggerFactory.getLogger(InnsynTjeneste.class);
    private final PDLConnection pdl;
    private final InnsynConnection innsyn;

    public InnsynTjeneste(InnsynConnection innsyn, PDLConnection pdl) {
        this.innsyn = innsyn;
        this.pdl = pdl;
    }

    @Override
    public String ping() {
        return innsyn.ping();
    }

    @Override
    public Saker saker(Fødselsnummer fnr) {
        var aktørId = pdl.aktørId(fnr);
        var saker = innsyn.saker(aktørId);
        return konvertAktørIdsTilFnr(saker, fnr);
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
            annenPartAktørId = pdl.aktørId(annenPartVedtakIdentifikator.annenPartFødselsnummer());
        } catch (Exception e) {
            LOG.warn("Feil ved mapping fra fnr til aktørid for annen part. Returnerer tomt resultat", e);
            return Optional.empty();
        }
        var barnAktørId = pdl.aktørId(annenPartVedtakIdentifikator.barnFødselsnummer());
        var request = new AnnenPartVedtakRequest(søker, annenPartAktørId, barnAktørId, annenPartVedtakIdentifikator.familiehendelse());
        var vedtak = innsyn.annenPartVedtak(request);
        LOG.info("Returnerer annen parts vedtak. Antall perioder {}", vedtak.map(v -> v.perioder().size()).orElse(0));
        return vedtak;
    }

    private Saker konvertAktørIdsTilFnr(Saker saker, Fødselsnummer fnr) {
        var beriketFpSaker = saker.foreldrepenger().stream()
            .map(sak -> konvertAktørIdsTilFnr(sak, fnr))
            .collect(Collectors.toSet());
        return new Saker(beriketFpSaker, saker.engangsstønad(), saker.svangerskapspenger());
    }

    private FpSak konvertAktørIdsTilFnr(FpSak sak, Fødselsnummer fnr) {
        var søker = pdl.hentPersonMedAlleBarn(fnr);
        return new FpSak(sak.saksnummer(), sak.sakAvsluttet(), sak.sisteSøknadMottattDato(), sak.kanSøkeOmEndring(),
            sak.sakTilhørerMor(), sak.gjelderAdopsjon(), sak.morUføretrygd(), sak.harAnnenForelderTilsvarendeRettEØS(),
            sak.ønskerJustertUttakVedFødsel(), sak.rettighetType(),
            berik(sak.annenPart()), sak.familiehendelse(), sak.gjeldendeVedtak(), sak.åpenBehandling(),
            barn(sak.barn(), søker.barn()), sak.dekningsgrad(), sak.oppdatertTidspunkt());
    }

    private Set<Person> barn(Set<Person> barn, List<Barn> søkerBarn) {
        return barn.stream()
            .map(b -> {
                var fødselsnummer = pdl.fnr(b.aktørId());
                return søkerBarn.stream()
                    .filter(sb -> Objects.equals(sb.fnr(), fødselsnummer))
                    .findFirst()
                    .map(bb -> new Person(bb.fnr(), null))
                    //Null her kan være adressebeskyttet barn, de er filtrert ut  i søkerBarn
                    .orElse(null);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    private Person berik(Person annenPart) {
        if (annenPart == null) {
            return null;
        }
        var aktørId = annenPart.aktørId();
        var fødselsnummer = pdl.fnr(aktørId);
        return new Person(fødselsnummer, null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[oppslag=" + pdl + ", innsyn=" + innsyn + "]";
    }

}
