package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static java.util.function.Predicate.not;
import static no.nav.foreldrepenger.common.domain.felles.medlemskap.ArbeidsInformasjon.IKKE_ARBEIDET;
import static no.nav.foreldrepenger.common.util.Constants.UKJENT_KODEVERKSVERDI;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.xml.bind.JAXBElement;

import com.google.common.collect.Iterables;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Søker;
import no.nav.foreldrepenger.common.domain.felles.DokumentType;
import no.nav.foreldrepenger.common.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.common.domain.felles.PåkrevdVedlegg;
import no.nav.foreldrepenger.common.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.VedleggMetaData;
import no.nav.foreldrepenger.common.domain.felles.ÅpenPeriode;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.common.domain.felles.opptjening.AnnenOpptjeningType;
import no.nav.foreldrepenger.common.domain.felles.opptjening.EgenNæring;
import no.nav.foreldrepenger.common.domain.felles.opptjening.FrilansOppdrag;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Regnskapsfører;
import no.nav.foreldrepenger.common.domain.felles.opptjening.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Virksomhetstype;
import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.OppholdUtlandet;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Periode;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.AnnenOpptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.EgenNaering;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Frilans;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Frilansoppdrag;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.NorskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Regnskapsfoerer;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.UtenlandskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Innsendingstype;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Land;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Virksomhetstyper;
import no.nav.vedtak.felles.xml.soeknad.v3.OmYtelse;

public class V3XMLMapperCommon {
    private V3XMLMapperCommon() {
    }

    static List<Vedlegg> tilVedlegg(List<no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg> påkrevd,
            List<no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg> valgfritt) {
        return Stream.concat(safeStream(valgfritt)
                .map(V3XMLMapperCommon::metadataFra)
                .map(s -> new ValgfrittVedlegg(s, null)),
                safeStream(påkrevd)
                        .map(V3XMLMapperCommon::metadataFra)
                        .map(s -> new PåkrevdVedlegg(s, null)))
                .toList();
    }

    private static VedleggMetaData metadataFra(no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg v) {
        return new VedleggMetaData(v.getId(), tilInnsendingsType(v.getInnsendingstype()), tilDokumentType(v.getSkjemanummer()));
    }

    private static DokumentType tilDokumentType(String skjemanummer) {
        return DokumentType.valueOf(skjemanummer);
    }

    private static InnsendingsType tilInnsendingsType(Innsendingstype innsendingstype) {
        return InnsendingsType.valueOf(innsendingstype.getKode());
    }

    static <T> T ytelse(OmYtelse omYtelse, Class<T> clazz) {
        Object element = ((JAXBElement<?>) Iterables.getOnlyElement(omYtelse.getAny())).getValue();
        return clazz.cast(element);
    }

    static Søker tilSøker(Bruker søker) {
        return new Søker(tilRolle(søker.getSoeknadsrolle().getKode()), Målform.standard());
    }

    static Medlemsskap tilMedlemsskap(Medlemskap m, LocalDate d) {
        return new Medlemsskap(new TidligereOppholdsInformasjon(IKKE_ARBEIDET, utenlandsOppholdFør(m.getOppholdUtlandet(), d)),
                new FramtidigOppholdsInformasjon(utenlandsOppholdEtter(m.getOppholdUtlandet(), d)));
    }

    private static BrukerRolle tilRolle(String kode) {
        return Optional.of(kode)
                .map(BrukerRolle::valueOf)
                .orElse(BrukerRolle.IKKE_RELEVANT);
    }

    private static List<Utenlandsopphold> utenlandsOppholdFør(List<OppholdUtlandet> opphold, LocalDate søknadsDato) {
        return utenlandsOpphold(opphold, før(søknadsDato));
    }

    private static List<Utenlandsopphold> utenlandsOppholdEtter(List<OppholdUtlandet> opphold, LocalDate søknadsDato) {
        return utenlandsOpphold(opphold, etter(søknadsDato));
    }

    private static List<Utenlandsopphold> utenlandsOpphold(List<OppholdUtlandet> opphold, Predicate<? super OppholdUtlandet> predicate) {
        return safeStream(opphold)
                .filter(predicate)
                .map(u -> new Utenlandsopphold(tilLand(u.getLand()),
                        new LukketPeriode(u.getPeriode().getFom(), u.getPeriode().getTom())))
                .toList();
    }

    private static Predicate<? super OppholdUtlandet> før(LocalDate søknadsDato) {
        return f -> f.getPeriode().getFom().isBefore(søknadsDato);
    }

    private static Predicate<? super OppholdUtlandet> etter(LocalDate søknadsDato) {
        return f -> f.getPeriode().getFom().isAfter(søknadsDato);
    }

    static CountryCode tilLand(Land land) {
        return tilLand(land, null);
    }

    static CountryCode tilLand(Land land, CountryCode defaultLand) {
        return Optional.ofNullable(land)
                .map(Land::getKode)
                .map(CountryCode::getByCode)
                .orElse(defaultLand);
    }

    static no.nav.foreldrepenger.common.domain.felles.opptjening.Opptjening tilOpptjening(Opptjening opptjening) {
        return Optional.ofNullable(opptjening)
                .map(o -> new no.nav.foreldrepenger.common.domain.felles.opptjening.Opptjening(
                        tilUtenlandskeArbeidsforhold(opptjening.getUtenlandskArbeidsforhold()),
                        tilEgenNæring(opptjening.getEgenNaering()),
                        tilAnnenOpptjening(opptjening.getAnnenOpptjening()),
                        tilFrilans(opptjening.getFrilans())))
                .orElse(null);
    }

    private static no.nav.foreldrepenger.common.domain.felles.opptjening.Frilans tilFrilans(Frilans frilans) {
        return Optional.ofNullable(frilans)
                .map(f -> new no.nav.foreldrepenger.common.domain.felles.opptjening.Frilans(
                        tilÅpenPeriode(f.getPeriode()),
                        f.isHarInntektFraFosterhjem(),
                        f.isErNyoppstartet(),
                        tilFrilansOppdrag(f.getFrilansoppdrag()),
                        List.of()))
                .orElse(null);
    }

    private static List<FrilansOppdrag> tilFrilansOppdrag(List<Frilansoppdrag> frilansoppdrag) {
        return safeStream(frilansoppdrag)
                .map(V3XMLMapperCommon::tilFrilansOppdrag)
                .toList();
    }

    private static FrilansOppdrag tilFrilansOppdrag(Frilansoppdrag fo) {
        return Optional.ofNullable(fo)
                .map(f -> new FrilansOppdrag(f.getOppdragsgiver(), tilÅpenPeriode(f.getPeriode())))
                .orElse(null);
    }

    private static ÅpenPeriode tilÅpenPeriode(List<Periode> periode) {
        return Optional.ofNullable(periode)
                .orElseGet(List::of)
                .stream()
                .findFirst()
                .map(V3XMLMapperCommon::tilÅpenPeriode).orElse(null);
    }

    private static ÅpenPeriode tilÅpenPeriode(Periode periode) {
        return Optional.ofNullable(periode)
                .map(p -> new ÅpenPeriode(p.getFom(), p.getTom()))
                .orElse(null);
    }

    private static List<no.nav.foreldrepenger.common.domain.felles.opptjening.AnnenOpptjening> tilAnnenOpptjening(
            List<AnnenOpptjening> annenOpptjening) {
        return safeStream(annenOpptjening)
                .map(V3XMLMapperCommon::tilAnnenOpptjening)
                .toList();
    }

    private static no.nav.foreldrepenger.common.domain.felles.opptjening.AnnenOpptjening tilAnnenOpptjening(AnnenOpptjening ao) {
        return Optional.ofNullable(ao)
                .map(a -> new no.nav.foreldrepenger.common.domain.felles.opptjening.AnnenOpptjening(
                        AnnenOpptjeningType.valueOf(a.getType().getKode()),
                        tilÅpenPeriode(a.getPeriode()), List.of()))
                .orElse(null);
    }

    private static List<EgenNæring> tilEgenNæring(List<EgenNaering> egenNaering) {
        return safeStream(egenNaering)
                .map(V3XMLMapperCommon::tilEgenNæring)
                .toList();
    }

    private static EgenNæring tilEgenNæring(EgenNaering egenNæring) {
        if (egenNæring == null) {
            return null;
        }
        if (egenNæring instanceof NorskOrganisasjon n) {
            return tilNorskNæring(n);
        }
        if (egenNæring instanceof UtenlandskOrganisasjon u) {
            return tilUtenlandskNæring(u);
        }
        throw new UnexpectedInputException("Ikke støttet arbeidsforhold " + egenNæring.getClass().getSimpleName());
    }

    private static no.nav.foreldrepenger.common.domain.felles.opptjening.UtenlandskOrganisasjon tilUtenlandskNæring(UtenlandskOrganisasjon u) {
        return no.nav.foreldrepenger.common.domain.felles.opptjening.UtenlandskOrganisasjon.builder()
                .registrertILand(tilLand(u.getRegistrertILand()))
                .orgName(u.getNavn())
                .beskrivelseEndring(u.getBeskrivelseAvEndring())
                .endringsDato(u.getEndringsDato())
                .erNyOpprettet(u.isErNyoppstartet())
                .erVarigEndring(u.isErVarigEndring())
                .erNyIArbeidslivet(u.isErNyIArbeidslivet())
                .næringsinntektBrutto(u.getNaeringsinntektBrutto().longValue())
                .nærRelasjon(u.isNaerRelasjon())
                .periode(tilÅpenPeriode(u.getPeriode()))
                .regnskapsførere(tilRegnskapsFørere(u.getRegnskapsfoerer()))
                .virksomhetsTyper(tilVirksomhetsTyper(u.getVirksomhetstype()))
                .build();
    }

    private static EgenNæring tilNorskNæring(NorskOrganisasjon n) {
        return no.nav.foreldrepenger.common.domain.felles.opptjening.NorskOrganisasjon.builder()
                .beskrivelseEndring(n.getBeskrivelseAvEndring())
                .endringsDato(n.getEndringsDato())
                .erNyOpprettet(n.isErNyoppstartet())
                .erVarigEndring(n.isErVarigEndring())
                .erNyIArbeidslivet(n.isErNyIArbeidslivet())
                .næringsinntektBrutto(n.getNaeringsinntektBrutto().longValue())
                .nærRelasjon(n.isNaerRelasjon())
                .orgName(n.getNavn())
                .orgNummer(n.getOrganisasjonsnummer())
                .periode(tilÅpenPeriode(n.getPeriode()))
                .regnskapsførere(tilRegnskapsFørere(n.getRegnskapsfoerer()))
                .virksomhetsTyper(tilVirksomhetsTyper(n.getVirksomhetstype()))
                .build();
    }

    private static List<Regnskapsfører> tilRegnskapsFørere(Regnskapsfoerer rf) {
        return Optional.ofNullable(rf)
                .map(r -> new Regnskapsfører(r.getNavn(), r.getTelefon()))
                .map(r -> List.of(r)).orElse(List.of());
    }

    private static List<UtenlandskArbeidsforhold> tilUtenlandskeArbeidsforhold(
            List<no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.UtenlandskArbeidsforhold> u) {
        return safeStream(u)
                .map(V3XMLMapperCommon::tilUtenlandskArbeidsforhold)
                .toList();
    }

    private static UtenlandskArbeidsforhold tilUtenlandskArbeidsforhold(
            no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.UtenlandskArbeidsforhold u) {
        return new UtenlandskArbeidsforhold(
                u.getArbeidsgiversnavn(),
                tilÅpenPeriode(u.getPeriode()),
                null,
                tilLand(u.getArbeidsland()));
    }

    private static List<Virksomhetstype> tilVirksomhetsTyper(List<Virksomhetstyper> v) {
        return safeStream(v)
                .map(V3XMLMapperCommon::tilVirksomhetsType)
                .toList();
    }

    private static Virksomhetstype tilVirksomhetsType(Virksomhetstyper type) {
        return Optional.ofNullable(type)
                .filter(not(t -> t.getKode().equals(UKJENT_KODEVERKSVERDI)))
                .map(Virksomhetstyper::getKode)
                .map(Virksomhetstype::valueOf)
                .orElse(null);
    }
}
