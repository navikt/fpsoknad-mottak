package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.Constants.UKJENT_KODEVERKSVERDI;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.xml.bind.JAXBElement;

import com.google.common.collect.Iterables;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.PåkrevdVedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.VedleggMetaData;
import no.nav.foreldrepenger.mottak.domain.felles.ÅpenPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjeningType;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.EgenNæring;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.FrilansOppdrag;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Regnskapsfører;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Virksomhetstype;
import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;
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
        Stream<Vedlegg> vf = safeStream(valgfritt)
                .map(V3XMLMapperCommon::metadataFra)
                .map(s -> new ValgfrittVedlegg(s, null));
        Stream<Vedlegg> pk = safeStream(påkrevd)
                .map(V3XMLMapperCommon::metadataFra)
                .map(s -> new PåkrevdVedlegg(s, null));
        return Stream.concat(vf, pk).collect(toList());
    }

    private static VedleggMetaData metadataFra(no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg vedlegg) {
        return new VedleggMetaData(
                vedlegg.getId(),
                tilInnsendingsType(vedlegg.getInnsendingstype()),
                tilDokumentType(vedlegg.getSkjemanummer()));
    }

    private static DokumentType tilDokumentType(String skjemanummer) {
        return DokumentType.valueOf(skjemanummer);
    }

    private static InnsendingsType tilInnsendingsType(Innsendingstype innsendingstype) {
        return InnsendingsType.valueOf(innsendingstype.getKode());
    }

    static <T> T ytelse(OmYtelse omYtelse, Class<T> clazz) {
        Object element = ((JAXBElement<?>) Iterables.getOnlyElement(omYtelse.getAny())).getValue();
        return (T) element;
    }

    static Søker tilSøker(Bruker søker) {
        return new Søker(tilRolle(søker.getSoeknadsrolle().getKode()));
    }

    static Medlemsskap tilMedlemsskap(Medlemskap medlemskap, LocalDate søknadsDato) {
        TidligereOppholdsInformasjon tidligere = new TidligereOppholdsInformasjon(ArbeidsInformasjon.IKKE_ARBEIDET,
                utenlandsOppholdFør(medlemskap.getOppholdUtlandet(), søknadsDato));
        FramtidigOppholdsInformasjon framtidig = new FramtidigOppholdsInformasjon(
                utenlandsOppholdEtter(medlemskap.getOppholdUtlandet(), søknadsDato));
        return new Medlemsskap(tidligere, framtidig);
    }

    private static BrukerRolle tilRolle(String kode) {
        return Optional.of(kode)
                .map(BrukerRolle::valueOf)
                .orElse(BrukerRolle.IKKE_RELEVANT);
    }

    private static List<Utenlandsopphold> utenlandsOppholdFør(List<OppholdUtlandet> opphold, LocalDate søknadsDato) {
        return utenlandsOpphold(opphold, søknadsDato, før(søknadsDato));
    }

    private static List<Utenlandsopphold> utenlandsOppholdEtter(List<OppholdUtlandet> opphold, LocalDate søknadsDato) {
        return utenlandsOpphold(opphold, søknadsDato, etter(søknadsDato));
    }

    private static List<Utenlandsopphold> utenlandsOpphold(List<OppholdUtlandet> opphold, LocalDate søknadsDato,
            Predicate<? super OppholdUtlandet> predicate) {
        return safeStream(opphold)
                .filter(predicate)
                .map(u -> new Utenlandsopphold(tilLand(u.getLand()),
                        new LukketPeriode(u.getPeriode().getFom(), u.getPeriode().getTom())))
                .collect(toList());
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

    static no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening tilOpptjening(
            Opptjening opptjening) {
        if (opptjening == null) {
            return null;
        }
        return new no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening(
                tilUtenlandskeArbeidsforhold(opptjening.getUtenlandskArbeidsforhold()),
                tilEgenNæring(opptjening.getEgenNaering()),
                tilAnnenOpptjening(opptjening.getAnnenOpptjening()),
                tilFrilans(opptjening.getFrilans()));
    }

    private static no.nav.foreldrepenger.mottak.domain.felles.opptjening.Frilans tilFrilans(Frilans frilans) {
        if (frilans == null) {
            return null;
        }
        return new no.nav.foreldrepenger.mottak.domain.felles.opptjening.Frilans(
                tilÅpenPeriode(frilans.getPeriode()),
                frilans.isHarInntektFraFosterhjem(),
                frilans.isErNyoppstartet(),
                tilFrilansOppdrag(frilans.getFrilansoppdrag()),
                emptyList());
    }

    private static List<FrilansOppdrag> tilFrilansOppdrag(List<Frilansoppdrag> frilansoppdrag) {
        return safeStream(frilansoppdrag)
                .map(V3XMLMapperCommon::tilFrilansOppdrag)
                .collect(toList());
    }

    private static FrilansOppdrag tilFrilansOppdrag(Frilansoppdrag frilansoppdrag) {
        if (frilansoppdrag == null) {
            return null;
        }
        return new FrilansOppdrag(
                frilansoppdrag.getOppdragsgiver(),
                tilÅpenPeriode(frilansoppdrag.getPeriode()));
    }

    private static ÅpenPeriode tilÅpenPeriode(List<Periode> periode) {
        return (periode == null) || periode.isEmpty() ? null : tilÅpenPeriode(periode.get(0));
    }

    private static ÅpenPeriode tilÅpenPeriode(Periode periode) {
        if (periode == null) {
            return null;
        }
        return new ÅpenPeriode(
                periode.getFom(),
                periode.getTom());
    }

    private static List<no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening> tilAnnenOpptjening(
            List<AnnenOpptjening> annenOpptjening) {
        return safeStream(annenOpptjening)
                .map(V3XMLMapperCommon::tilAnnenOpptjening)
                .collect(toList());
    }

    private static no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening tilAnnenOpptjening(
            AnnenOpptjening annenOpptjening) {
        if (annenOpptjening == null) {
            return null;
        }
        return new no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening(
                AnnenOpptjeningType.valueOf(annenOpptjening.getType().getKode()),
                tilÅpenPeriode(annenOpptjening.getPeriode()),
                emptyList());
    }

    private static List<EgenNæring> tilEgenNæring(List<EgenNaering> egenNaering) {
        return safeStream(egenNaering)
                .map(V3XMLMapperCommon::tilEgenNæring)
                .collect(toList());
    }

    private static EgenNæring tilEgenNæring(EgenNaering egenNæring) {
        if (egenNæring == null) {
            return null;
        }
        if (egenNæring instanceof NorskOrganisasjon) {
            NorskOrganisasjon norskOrg = NorskOrganisasjon.class.cast(egenNæring);
            return no.nav.foreldrepenger.mottak.domain.felles.opptjening.NorskOrganisasjon.builder()
                    .beskrivelseEndring(norskOrg.getBeskrivelseAvEndring())
                    .endringsDato(norskOrg.getEndringsDato())
                    .erNyOpprettet(norskOrg.isErNyoppstartet())
                    .erVarigEndring(norskOrg.isErVarigEndring())
                    .erNyIArbeidslivet(norskOrg.isErNyIArbeidslivet())
                    .næringsinntektBrutto(norskOrg.getNaeringsinntektBrutto().longValue())
                    .nærRelasjon(norskOrg.isNaerRelasjon())
                    .orgName(norskOrg.getNavn())
                    .orgNummer(norskOrg.getOrganisasjonsnummer())
                    .periode(tilÅpenPeriode(norskOrg.getPeriode()))
                    .regnskapsførere(tilRegnskapsFørere(norskOrg.getRegnskapsfoerer()))
                    .virksomhetsTyper(tilVirksomhetsTyper(norskOrg.getVirksomhetstype()))
                    .build();
        }
        if (egenNæring instanceof UtenlandskOrganisasjon) {
            UtenlandskOrganisasjon utenlandskOrg = UtenlandskOrganisasjon.class.cast(egenNæring);
            return no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskOrganisasjon.builder()
                    .registrertILand(tilLand(utenlandskOrg.getRegistrertILand()))
                    .orgName(utenlandskOrg.getNavn())
                    .beskrivelseEndring(utenlandskOrg.getBeskrivelseAvEndring())
                    .endringsDato(utenlandskOrg.getEndringsDato())
                    .erNyOpprettet(utenlandskOrg.isErNyoppstartet())
                    .erVarigEndring(utenlandskOrg.isErVarigEndring())
                    .erNyIArbeidslivet(utenlandskOrg.isErNyIArbeidslivet())
                    .næringsinntektBrutto(utenlandskOrg.getNaeringsinntektBrutto().longValue())
                    .nærRelasjon(utenlandskOrg.isNaerRelasjon())
                    .periode(tilÅpenPeriode(utenlandskOrg.getPeriode()))
                    .regnskapsførere(tilRegnskapsFørere(utenlandskOrg.getRegnskapsfoerer()))
                    .virksomhetsTyper(tilVirksomhetsTyper(utenlandskOrg.getVirksomhetstype()))
                    .build();
        }
        throw new UnexpectedInputException("Ikke"
                + " støttet arbeidsforhold " + egenNæring.getClass().getSimpleName());
    }

    private static List<Regnskapsfører> tilRegnskapsFørere(Regnskapsfoerer regnskapsfoerer) {
        if (regnskapsfoerer == null) {
            return emptyList();
        }
        return singletonList(new Regnskapsfører(
                regnskapsfoerer.getNavn(),
                regnskapsfoerer.getTelefon()));
    }

    private static List<UtenlandskArbeidsforhold> tilUtenlandskeArbeidsforhold(
            List<no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.UtenlandskArbeidsforhold> utenlandskeArbeidsforhold) {
        return safeStream(utenlandskeArbeidsforhold)
                .map(V3XMLMapperCommon::tilUtenlandskArbeidsforhold)
                .collect(toList());
    }

    private static UtenlandskArbeidsforhold tilUtenlandskArbeidsforhold(
            no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.UtenlandskArbeidsforhold arbeidforhold) {
        return new UtenlandskArbeidsforhold(
                arbeidforhold.getArbeidsgiversnavn(),
                tilÅpenPeriode(arbeidforhold.getPeriode()),
                null,
                tilLand(arbeidforhold.getArbeidsland()));
    }

    private static List<Virksomhetstype> tilVirksomhetsTyper(List<Virksomhetstyper> virksomhetstyper) {
        return safeStream(virksomhetstyper)
                .map(V3XMLMapperCommon::tilVirksomhetsType)
                .collect(toList());
    }

    private static Virksomhetstype tilVirksomhetsType(Virksomhetstyper type) {
        if ((type == null) || type.getKode().equals(UKJENT_KODEVERKSVERDI)) {
            return null;
        }
        return Virksomhetstype.valueOf(type.getKode());
    }
}
