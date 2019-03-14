package no.nav.foreldrepenger.mottak.innsending.mappers;

import static com.neovisionaries.i18n.CountryCode.XK;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.SEND_SENERE;
import static no.nav.foreldrepenger.mottak.domain.felles.SpråkKode.defaultSpråk;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.springframework.util.CollectionUtils;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.SpråkKode;
import no.nav.foreldrepenger.mottak.domain.felles.ÅpenPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjeningType;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.EgenNæring;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.FrilansOppdrag;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Regnskapsfører;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Virksomhetstype;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.OppholdUtlandet;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Periode;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.AnnenOpptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.EgenNaering;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Frilans;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Frilansoppdrag;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.NorskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Regnskapsfoerer;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.UtenlandskArbeidsforhold;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.UtenlandskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.AnnenOpptjeningTyper;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Brukerroller;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Innsendingstype;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Land;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Spraakkode;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Virksomhetstyper;

final class V3DomainMapperUtils {

    private static final Land KOSOVO = landFra(DomainMapper.KOSOVO);

    private static final no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.ObjectFactory FP_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.ObjectFactory();

    private V3DomainMapperUtils() {

    }

    static Spraakkode språkFra(Søker søker) {
        return Optional.ofNullable(søker)
                .map(Søker::getSpråkkode)
                .map(SpråkKode::name)
                .map(V3DomainMapperUtils::språkKodeFra)
                .orElse(defaultSpråkKode());
    }

    static Periode periodeFra(ÅpenPeriode periode) {
        return Optional.ofNullable(periode)
                .map(p -> new Periode()
                        .withFom(p.getFom())
                        .withTom(p.getTom()))
                .orElse(null);
    }

    static Opptjening opptjeningFra(
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening opptjening) {
        return new Opptjening()
                .withUtenlandskArbeidsforhold(utenlandskeArbeidsforholdFra(opptjening.getUtenlandskArbeidsforhold()))
                .withFrilans(frilansFra(opptjening.getFrilans()))
                .withEgenNaering(egneNæringerFra(opptjening.getEgenNæring()))
                .withAnnenOpptjening(andreOpptjeningerFra(opptjening.getAnnenOpptjening()));
    }

    static Medlemskap medlemsskapFra(Medlemsskap ms, LocalDate relasjonsDato) {
        if (ms != null) {
            return new Medlemskap()
                    .withOppholdUtlandet(oppholdUtlandetFra(ms))
                    .withINorgeVedFoedselstidspunkt(ms.varINorge(relasjonsDato))
                    .withBoddINorgeSiste12Mnd(oppholdINorgeSiste12(ms))
                    .withBorINorgeNeste12Mnd(oppholdINorgeNeste12(ms));
        }
        return null;
    }

    private static boolean oppholdINorgeSiste12(Medlemsskap ms) {
        return ms.getTidligereOppholdsInfo().getUtenlandsOpphold().isEmpty();
    }

    private static boolean oppholdINorgeNeste12(Medlemsskap ms) {
        return ms.getFramtidigOppholdsInfo().getUtenlandsOpphold().isEmpty();
    }

    private static List<OppholdUtlandet> oppholdUtlandetFra(Medlemsskap ms) {
        return safeStream(ms.utenlandsOpphold())
                .map(V3DomainMapperUtils::utenlandOppholdFra)
                .collect(toList());
    }

    private static OppholdUtlandet utenlandOppholdFra(Utenlandsopphold opphold) {
        return opphold == null ? null
                : new OppholdUtlandet()
                        .withPeriode(new Periode()
                                .withFom(opphold.getFom())
                                .withTom(opphold.getTom()))
                        .withLand(landFra(opphold.getLand()));
    }

    private static List<Virksomhetstyper> virksomhetsTyperFra(List<Virksomhetstype> typer) {
        return safeStream(typer)
                .map(V3DomainMapperUtils::virksomhetsTypeFra)
                .collect(toList());
    }

    private static Virksomhetstyper virksomhetsTypeFra(Virksomhetstype type) {
        return Optional.ofNullable(type)
                .map(s -> virksomhetsTypeFra(s.name()))
                .orElse(null);
    }

    private static Virksomhetstyper virksomhetsTypeFra(String type) {
        Virksomhetstyper vt = new Virksomhetstyper().withKode(type);
        vt.setKodeverk(vt.getKodeverk());
        return vt;
    }

    private static List<EgenNaering> egneNæringerFra(List<EgenNæring> egenNæring) {
        return safeStream(egenNæring)
                .map(V3DomainMapperUtils::egenNæringFra)
                .collect(toList());
    }

    private static EgenNaering egenNæringFra(EgenNæring egenNæring) {

        if (egenNæring instanceof no.nav.foreldrepenger.mottak.domain.felles.opptjening.NorskOrganisasjon) {
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.NorskOrganisasjon norskOrg = no.nav.foreldrepenger.mottak.domain.felles.opptjening.NorskOrganisasjon.class
                    .cast(egenNæring);
            return new NorskOrganisasjon()
                    .withVedlegg(egenNæringVedleggFraIDs(norskOrg.getVedlegg()))
                    .withBeskrivelseAvEndring(norskOrg.getBeskrivelseEndring())
                    .withNaerRelasjon(norskOrg.isNærRelasjon())
                    .withEndringsDato(norskOrg.getEndringsDato())
                    .withOppstartsdato(norskOrg.getOppstartsDato())
                    .withErNyoppstartet(norskOrg.isErNyOpprettet())
                    .withErNyIArbeidslivet(norskOrg.isErNyIArbeidslivet())
                    .withErVarigEndring(norskOrg.isErVarigEndring())
                    .withNaeringsinntektBrutto(BigInteger.valueOf(norskOrg.getNæringsinntektBrutto()))
                    .withNavn(norskOrg.getOrgName())
                    .withOrganisasjonsnummer(norskOrg.getOrgNummer())
                    .withPeriode(periodeFra(norskOrg.getPeriode()))
                    .withRegnskapsfoerer(regnskapsFørerFra(norskOrg.getRegnskapsførere()))
                    .withVirksomhetstype(virksomhetsTyperFra(norskOrg.getVirksomhetsTyper()))
                    .withOppstartsdato(norskOrg.getOppstartsDato());
        }
        if (egenNæring instanceof no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskOrganisasjon) {
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskOrganisasjon utenlandskOrg = no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskOrganisasjon.class
                    .cast(egenNæring);
            return new UtenlandskOrganisasjon()
                    .withVedlegg(egenNæringVedleggFraIDs(utenlandskOrg.getVedlegg()))
                    .withBeskrivelseAvEndring(utenlandskOrg.getBeskrivelseEndring())
                    .withNaerRelasjon(utenlandskOrg.isNærRelasjon())
                    .withEndringsDato(utenlandskOrg.getEndringsDato())
                    .withOppstartsdato(utenlandskOrg.getOppstartsDato())
                    .withErNyoppstartet(utenlandskOrg.isErNyOpprettet())
                    .withErNyIArbeidslivet(utenlandskOrg.isErNyIArbeidslivet())
                    .withErVarigEndring(utenlandskOrg.isErVarigEndring())
                    .withNaeringsinntektBrutto(BigInteger.valueOf(utenlandskOrg.getNæringsinntektBrutto()))
                    .withNavn(utenlandskOrg.getOrgName())
                    .withRegistrertILand(landFra(utenlandskOrg.getRegistrertILand()))
                    .withPeriode(periodeFra(utenlandskOrg.getPeriode()))
                    .withRegnskapsfoerer(regnskapsFørerFra(utenlandskOrg.getRegnskapsførere()))
                    .withVirksomhetstype(virksomhetsTyperFra(utenlandskOrg.getVirksomhetsTyper()));
        }
        throw new UnexpectedInputException("Vil aldri skje");
    }

    private static List<JAXBElement<Object>> egenNæringVedleggFraIDs(List<String> vedlegg) {
        return safeStream(vedlegg)
                .map(s -> FP_FACTORY_V3.createEgenNaeringVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static List<AnnenOpptjening> andreOpptjeningerFra(
            List<no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening> annenOpptjening) {
        return safeStream(annenOpptjening)
                .map(V3DomainMapperUtils::annenOpptjeningFra)
                .collect(toList());
    }

    private static AnnenOpptjening annenOpptjeningFra(
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening annen) {
        return Optional.ofNullable(annen)
                .map(V3DomainMapperUtils::create)
                .orElse(null);
    }

    private static AnnenOpptjeningTyper create(String kode) {
        AnnenOpptjeningTyper type = new AnnenOpptjeningTyper().withKode(kode);
        type.setKodeverk(type.getKodeverk());
        return type;
    }

    private static AnnenOpptjening create(no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening annen) {
        return new AnnenOpptjening()
                .withVedlegg(annenOpptjeningVedleggFra(annen.getVedlegg()))
                .withType(annenOpptjeningTypeFra(annen.getType()))
                .withPeriode(periodeFra(annen.getPeriode()));
    }

    private static AnnenOpptjeningTyper annenOpptjeningTypeFra(AnnenOpptjeningType type) {
        return Optional.ofNullable(type)
                .map(AnnenOpptjeningType::name)
                .map(V3DomainMapperUtils::create)
                .orElse(null);
    }

    private static Regnskapsfoerer regnskapsFørerFra(List<Regnskapsfører> regnskapsførere) {
        if (CollectionUtils.isEmpty(regnskapsførere)) {
            return null;
        }
        Regnskapsfører regnskapsfører = regnskapsførere.get(0);
        return new Regnskapsfoerer()
                .withTelefon(regnskapsfører.getTelefon())
                .withNavn(regnskapsfører.getNavn());
    }

    static Land landFra(CountryCode land) {
        if (XK.equals(land)) {
            return KOSOVO; // https://jira.adeo.no/browse/PFP-6077
        }
        return Optional.ofNullable(land)
                .map(s -> landFra(s.getAlpha3()))
                .orElse(null);
    }

    private static List<UtenlandskArbeidsforhold> utenlandskeArbeidsforholdFra(
            List<no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskArbeidsforhold> arbeidsforhold) {
        return safeStream(arbeidsforhold)
                .map(V3DomainMapperUtils::utenlandskArbeidsforholdFra)
                .collect(toList());
    }

    private static no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.UtenlandskArbeidsforhold utenlandskArbeidsforholdFra(
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskArbeidsforhold forhold) {
        return new UtenlandskArbeidsforhold()
                .withVedlegg(utenlandsArbeidsforholdVedleggFra(forhold.getVedlegg()))
                .withArbeidsgiversnavn(forhold.getArbeidsgiverNavn())
                .withArbeidsland(landFra(forhold.getLand()))
                .withPeriode(periodeFra(forhold.getPeriode()));
    }

    private static List<JAXBElement<Object>> utenlandsArbeidsforholdVedleggFra(List<String> vedlegg) {
        return safeStream(vedlegg)
                .map(s -> FP_FACTORY_V3.createUtenlandskArbeidsforholdVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    static Bruker søkerFra(AktorId aktørId, Søker søker) {
        return new Bruker()
                .withAktoerId(aktørId.getId())
                .withSoeknadsrolle(brukerRolleFra(søker.getSøknadsRolle()));
    }

    static List<Vedlegg> vedleggFra(
            List<? extends no.nav.foreldrepenger.mottak.domain.felles.Vedlegg> vedlegg) {
        return safeStream(vedlegg)
                .map(V3DomainMapperUtils::vedleggFra)
                .collect(toList());
    }

    private static Innsendingstype innsendingstypeFra(InnsendingsType innsendingsType) {

        switch (innsendingsType) {
        case SEND_SENERE:
            return innsendingsTypeMedKodeverk(SEND_SENERE);
        case LASTET_OPP:
            return innsendingsTypeMedKodeverk(LASTET_OPP);
        default:
            throw new UnexpectedInputException("Innsendingstype " + innsendingsType + "  ikke støttet");
        }
    }

    private static Frilans frilansFra(no.nav.foreldrepenger.mottak.domain.felles.opptjening.Frilans frilans) {
        return Optional.ofNullable(frilans)
                .map(V3DomainMapperUtils::create)
                .orElse(null);
    }

    private static Frilans create(no.nav.foreldrepenger.mottak.domain.felles.opptjening.Frilans frilans) {
        return new Frilans()
                .withVedlegg(frilansVedleggFraIDs(frilans.getVedlegg()))
                .withErNyoppstartet(frilans.isNyOppstartet())
                .withHarInntektFraFosterhjem(frilans.isHarInntektFraFosterhjem())
                .withNaerRelasjon(!CollectionUtils.isEmpty(frilans.getFrilansOppdrag()))
                .withPeriode(periodeFra(frilans.getPeriode()))
                .withFrilansoppdrag(frilansOppdragFra(frilans.getFrilansOppdrag()));
    }

    private static List<Frilansoppdrag> frilansOppdragFra(List<FrilansOppdrag> frilansOppdrag) {
        return safeStream(frilansOppdrag)
                .map(V3DomainMapperUtils::frilansOppdragFra)
                .collect(toList());
    }

    private static Frilansoppdrag frilansOppdragFra(FrilansOppdrag oppdrag) {
        return Optional.ofNullable(oppdrag)
                .map(V3DomainMapperUtils::create)
                .orElse(null);
    }

    private static Frilansoppdrag create(FrilansOppdrag oppdrag) {
        return new Frilansoppdrag()
                .withOppdragsgiver(oppdrag.getOppdragsgiver())
                .withPeriode(periodeFra(oppdrag.getPeriode()));
    }

    private static List<JAXBElement<Object>> frilansVedleggFraIDs(List<String> vedlegg) {
        return safeStream(vedlegg)
                .map(s -> FP_FACTORY_V3.createFrilansVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static List<JAXBElement<Object>> annenOpptjeningVedleggFra(List<String> vedlegg) {
        return safeStream(vedlegg)
                .map(s -> FP_FACTORY_V3.createAnnenOpptjeningVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static Vedlegg vedleggFra(no.nav.foreldrepenger.mottak.domain.felles.Vedlegg vedlegg) {
        return new Vedlegg()
                .withId(vedlegg.getId())
                .withTilleggsinformasjon(vedlegg.getBeskrivelse())
                .withSkjemanummer(vedlegg.getDokumentType().name())
                .withInnsendingstype(innsendingstypeFra(vedlegg.getInnsendingsType()));
    }

    private static Spraakkode defaultSpråkKode() {
        return språkKodeFra(defaultSpråk());
    }

    private static Spraakkode språkKodeFra(SpråkKode kode) {
        return språkKodeFra(kode.name());
    }

    private static Spraakkode språkKodeFra(String kode) {
        return new Spraakkode().withKode(kode);
    }

    private static Innsendingstype innsendingsTypeMedKodeverk(InnsendingsType type) {
        Innsendingstype typeMedKodeverk = new Innsendingstype().withKode(type.name());
        return typeMedKodeverk.withKodeverk(typeMedKodeverk.getKodeverk());
    }

    private static Brukerroller brukerRolleFra(BrukerRolle søknadsRolle) {
        return brukerRolleFra(søknadsRolle.name());
    }

    private static Brukerroller brukerRolleFra(String rolle) {
        Brukerroller brukerRolle = new Brukerroller().withKode(rolle);
        return brukerRolle.withKodeverk(brukerRolle.getKodeverk());
    }

    private static Land landFra(String alpha3) {
        Land land = new Land().withKode(alpha3);
        return land.withKodeverk(land.getKodeverk());
    }
}
