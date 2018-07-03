package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.Jaxb.marshall;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenOpptjeningType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.EgenNæring;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.GradertUttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.MorsAktivitet;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OppholdsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Oppholdsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OverføringsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Overføringsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Regnskapsfører;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.RelasjonTilBarnMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.StønadskontoType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtsettelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ÅpenPeriode;
import no.nav.foreldrepenger.mottak.util.Jaxb;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelderMedNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelderUtenNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Foedsel;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.LukketPeriode;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.OppholdNorge;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.OppholdUtlandet;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Periode;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Rettigheter;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.SoekersRelasjonTilBarnet;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Termin;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.UkjentForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Vedlegg;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Ytelse;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.AnnenOpptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Dekningsgrad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.EgenNaering;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.NorskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Regnskapsfoerer;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.UtenlandskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.AnnenOpptjeningTyper;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Brukerroller;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Dekningsgrader;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Innsendingstype;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Land;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.MorsAktivitetsTyper;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Oppholdsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Overfoeringsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Utsettelsesaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Uttaksperiodetyper;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Virksomhetstyper;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Gradering;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Oppholdsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Overfoeringsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Utsettelsesperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Uttaksperiode;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

@Component
public class FPFordelSøknadGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelSøknadGenerator.class);
    private static final String LASTET_OPP = "LASTET_OPP";
    private static final JAXBContext CONTEXT = Jaxb.context(Soeknad.class);

    public String toXML(Søknad søknad, AktorId aktørId) {
        return toXML(toFPFordelModel(søknad, aktørId));
    }

    public Soeknad toFPFordelModel(Søknad søknad, AktorId aktørId) {
        LOG.info("Genererer FPFordel modell fra {}", søknad);
        return new Soeknad()
                .withAndreVedlegg(vedleggFra(søknad.getFrivilligeVedlegg()))
                .withPaakrevdeVedlegg(vedleggFra(søknad.getPåkrevdeVedlegg()))
                .withSoeker(søkerFra(aktørId, søknad.getSøker()))
                .withOmYtelse(ytelseFra(søknad))
                .withMottattDato(søknad.getMottattdato().toLocalDate())
                .withBegrunnelseForSenSoeknad(søknad.getBegrunnelseForSenSøknad())
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger());
    }

    private List<Vedlegg> vedleggFra(List<? extends no.nav.foreldrepenger.mottak.domain.felles.Vedlegg> vedlegg) {
        return vedlegg.stream()
                .map(this::vedleggFra)
                .collect(toList());
    }

    private Vedlegg vedleggFra(no.nav.foreldrepenger.mottak.domain.felles.Vedlegg vedlegg) {
        return new Vedlegg()
                .withTilleggsinformasjon(vedlegg.getMetadata().getBeskrivelse())
                .withSkjemanummer(vedlegg.getMetadata().getSkjemanummer().id)
                .withInnsendingstype(innsendingsTypeMedKodeverk());
    }

    private static Innsendingstype innsendingsTypeMedKodeverk() {
        Innsendingstype type = new Innsendingstype().withKode(LASTET_OPP);
        return type.withKodeverk(type.getKodeverk());
    }

    private static Ytelse ytelseFra(Søknad søknad) {
        no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger ytelse = no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.class
                .cast(søknad.getYtelse());
        LOG.info("Genererer FPFordel ytelse modell fra {}", ytelse);
        return new Foreldrepenger()
                .withDekningsgrad(dekningsgradFra(ytelse.getDekningsgrad()))
                .withMedlemskap(medlemsskapFra(ytelse.getMedlemsskap()))
                .withOpptjening(opptjeningFra(ytelse.getOpptjening()))
                .withFordeling(fordelingFra(ytelse.getFordeling()))
                .withRettigheter(rettighetrFra(ytelse.getRettigheter(), ytelse
                        .getAnnenForelder() instanceof no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder))
                .withAnnenForelder(annenForelderFra(ytelse.getAnnenForelder()))
                .withRelasjonTilBarnet(relasjonFra(ytelse.getRelasjonTilBarn()));
    }

    private static Dekningsgrad dekningsgradFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad dekningsgrad) {
        return new Dekningsgrad()
                .withDekningsgrad(dekningsgraderMedKodeverk(dekningsgrad.kode()));
    }

    private static Dekningsgrader dekningsgraderMedKodeverk(String kode) {
        Dekningsgrader dekningsgrad = new Dekningsgrader().withKode(kode);
        return dekningsgrad.withKodeverk(dekningsgrad.getKodeverk());
    }

    private static Opptjening opptjeningFra(no.nav.foreldrepenger.mottak.domain.foreldrepenger.Opptjening opptjening) {
        return opptjening == null ? null
                : new Opptjening()
                        .withEgenNaering(egenNæringFra(opptjening.getEgenNæring()))
                        .withUtenlandskArbeidsforhold(
                                utenlandskArbeidsforholdFra(opptjening.getUtenlandskArbeidsforhold()))
                        .withAnnenOpptjening(annenOpptjeningFra(opptjening.getAnnenOpptjening()));
    }

    private static List<EgenNaering> egenNæringFra(List<EgenNæring> egenNæring) {
        return egenNæring.stream()
                .map(FPFordelSøknadGenerator::egenNæringFra)
                .collect(toList());
    }

    private static List<no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.UtenlandskArbeidsforhold> utenlandskArbeidsforholdFra(
            List<no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskArbeidsforhold> utenlandskArbeidsforhold) {
        return utenlandskArbeidsforhold.stream()
                .map(FPFordelSøknadGenerator::utenlandskArbeidsforholdFra)
                .collect(toList());
    }

    private static List<AnnenOpptjening> annenOpptjeningFra(
            List<no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenOpptjening> annenOpptjening) {
        return annenOpptjening.stream()
                .map(FPFordelSøknadGenerator::annenOpptjeningFra)
                .collect(toList());
    }

    private static EgenNaering egenNæringFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.EgenNæring egenNæring) {
        if (egenNæring instanceof no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskOrganisasjon) {
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskOrganisasjon norskOrg = no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskOrganisasjon.class
                    .cast(egenNæring);
            return new NorskOrganisasjon()
                    .withBeskrivelseAvEndring(norskOrg.getBeskrivelseEndring())
                    .withNaerRelasjon(norskOrg.isNærRelasjon())
                    .withEndringsDato(norskOrg.getEndringsDato())
                    .withErNyoppstartet(norskOrg.isErNyOpprettet())
                    .withErVarigEndring(norskOrg.isErVarigEndring())
                    .withNaeringsinntektBrutto(BigInteger.valueOf(norskOrg.getNæringsinntektBrutto()))
                    .withNavn(norskOrg.getOrgName())
                    .withOrganisasjonsnummer(norskOrg.getOrgNummer())
                    .withPeriode(periodeFra(norskOrg.getPeriode()))
                    .withRegnskapsfoerer(regnskapsFørerFra(norskOrg.getRegnskapsfører()))
                    .withVirksomhetstype(virksomhetsTyperFra(norskOrg.getVirksomhetsTyper()))
                    .withArbeidsland(landFra(norskOrg.getArbeidsland()));
        }
        if (egenNæring instanceof no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskOrganisasjon) {
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskOrganisasjon utenlandskOrg = no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskOrganisasjon.class
                    .cast(egenNæring);
            return new UtenlandskOrganisasjon()
                    .withBeskrivelseAvEndring(utenlandskOrg.getBeskrivelseEndring())
                    .withNaerRelasjon(utenlandskOrg.isNærRelasjon())
                    .withEndringsDato(utenlandskOrg.getEndringsDato())
                    .withErNyoppstartet(utenlandskOrg.isErNyOpprettet())
                    .withErVarigEndring(utenlandskOrg.isErVarigEndring())
                    .withNaeringsinntektBrutto(BigInteger.valueOf(utenlandskOrg.getNæringsinntektBrutto()))
                    .withNavn(utenlandskOrg.getOrgName())
                    .withPeriode(periodeFra(utenlandskOrg.getPeriode()))
                    .withRegnskapsfoerer(regnskapsFørerFra(utenlandskOrg.getRegnskapsfører()))
                    .withVirksomhetstype(virksomhetsTyperFra(utenlandskOrg.getVirksomhetsTyper()))
                    .withArbeidsland(landFra(utenlandskOrg.getArbeidsland()));
        }
        throw new IllegalArgumentException("Vil aldri skje");

    }

    private static List<Virksomhetstyper> virksomhetsTyperFra(
            List<no.nav.foreldrepenger.mottak.domain.foreldrepenger.Virksomhetstype> typer) {
        return typer.stream()
                .map(FPFordelSøknadGenerator::virksomhetsTypeFra)
                .collect(Collectors.toList());
    }

    private static Virksomhetstyper virksomhetsTypeFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.Virksomhetstype type) {
        if (type == null) {
            return null;
        }
        switch (type) {
        case ANNEN:
        case DAGMAMMA:
        case FISKE:
        case JORDBRUK_SKOGBRUK:
            Virksomhetstyper vt = new Virksomhetstyper().withKode(type.name());
            vt.setKodeverk(vt.getKodeverk());
            return vt;
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    private static Regnskapsfoerer regnskapsFørerFra(Regnskapsfører regnskapsfører) {
        return regnskapsfører == null ? null
                : new Regnskapsfoerer()
                        .withTelefon(regnskapsfører.getTelefon())
                        .withNavn(navnFra(regnskapsfører.getNavn()));
    }

    private static String navnFra(Navn navn) {
        return navn == null ? null
                : (formatNavn(navn.getFornavn()) + " "
                        + formatNavn(navn.getMellomnavn()) + " "
                        + formatNavn(navn.getEtternavn()) + " ").trim();
    }

    private static String formatNavn(String navn) {
        return Optional.ofNullable(navn).orElse("");
    }

    private static AnnenOpptjening annenOpptjeningFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenOpptjening annenOpptjening) {
        return new AnnenOpptjening()
                .withType(opptjeningtypeFra(annenOpptjening.getType()))
                .withPeriode(periodeFra(annenOpptjening.getPeriode()));
    }

    private static no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.UtenlandskArbeidsforhold utenlandskArbeidsforholdFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskArbeidsforhold arbeidsForhold) {
        return utenlandskArbeidsforhold(arbeidsForhold);

    }

    private static no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.UtenlandskArbeidsforhold utenlandskArbeidsforhold(
            UtenlandskArbeidsforhold arbeidsForhold) {
        return new no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.UtenlandskArbeidsforhold()
                .withArbeidsgiversnavn(arbeidsForhold.getArbeidsgiverNavn())
                .withArbeidsland(landFra(arbeidsForhold.getLand()))
                .withPeriode(periodeFra(arbeidsForhold.getPeriode()));
    }

    private static AnnenOpptjeningTyper opptjeningtypeFra(AnnenOpptjeningType type) {
        if (type == null) {
            return null;
        }
        switch (type) {
        case ETTERLØNN_ARBEIDSGIVER:
        case LØNN_UNDER_UTDANNING:
        case MILITÆR_ELLER_SIVILTJENESTE:
        case VENTELØNN:
            return annenOpptjeningTypeMedKodeverk(type.name());
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    private static AnnenOpptjeningTyper annenOpptjeningTypeMedKodeverk(String kode) {
        AnnenOpptjeningTyper type = new AnnenOpptjeningTyper().withKode(kode);
        type.setKodeverk(type.getKodeverk());
        return type;
    }

    private static Periode periodeFra(ÅpenPeriode periode) {
        return periode == null ? null : new Periode().withFom(periode.getFom());
    }

    private static Medlemskap medlemsskapFra(Medlemsskap medlemsskap) {
        LOG.info("Genererer FPFordel medlemsskap modell fra {}", medlemsskap);
        Medlemskap m = new Medlemskap()
                .withOppholdUtlandet(oppholdUtlandetFra(medlemsskap.getTidligereOppholdsInfo(),
                        medlemsskap.getFramtidigOppholdsInfo()))
                .withINorgeVedFoedselstidspunkt(true);
        return (medlemsskap.getTidligereOppholdsInfo().isBoddINorge()
                && medlemsskap.getFramtidigOppholdsInfo().isNorgeNeste12())
                        ? m.withOppholdNorge(oppholdNorgeFra())
                        : m;

        // TODO sette norske perioder for de periodene man ikke er i utlandet?
    }

    private static List<OppholdNorge> oppholdNorgeFra() {
        return Collections.singletonList(new OppholdNorge()
                .withPeriode(new Periode()
                        .withFom(LocalDate.now().minusYears(1))
                        .withTom(LocalDate.now().plusYears(1))));
    }

    private static List<OppholdUtlandet> oppholdUtlandetFra(TidligereOppholdsInformasjon tidligereOppholdsInfo,
            FramtidigOppholdsInformasjon framtidigOppholdsInfo) {
        if (tidligereOppholdsInfo.isBoddINorge() && framtidigOppholdsInfo.isNorgeNeste12()) {
            return Collections.emptyList();
        }
        return Stream
                .concat(safeStream(tidligereOppholdsInfo.getUtenlandsOpphold()),
                        safeStream(framtidigOppholdsInfo.getUtenlandsOpphold()))
                .map(s -> utenlandOppholdFra(s))
                .collect(toList());

    }

    private static Stream<Utenlandsopphold> safeStream(List<Utenlandsopphold> list) {
        return list == null ? Stream.empty() : list.stream();

    }

    private static List<OppholdUtlandet> oppholdUtlandetFra(List<Utenlandsopphold> utenlandsOpphold) {
        return utenlandsOpphold.stream()
                .map(s -> utenlandOppholdFra(s))
                .collect(toList());
    }

    private static OppholdUtlandet utenlandOppholdFra(Utenlandsopphold opphold) {
        return opphold == null ? null
                : new OppholdUtlandet()
                        .withLand(landFra(opphold.getLand()));
    }

    private static final Land landFra(CountryCode land) {
        return land == null ? null : landWithKodeverk(land.getAlpha3());
    }

    private static final Land landWithKodeverk(String alphq3) {
        Land land = new Land().withKode(alphq3);
        return land.withKodeverk(land.getKodeverk());
    }

    private static Fordeling fordelingFra(no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fordeling fordeling) {
        if (fordeling == null) {
            return null;
        }
        return new Fordeling()
                .withPerioder(perioderFra(fordeling.getPerioder()))
                .withOenskerKvoteOverfoert(overføringsÅrsakFra(fordeling.getØnskerKvoteOverført()))
                .withAnnenForelderErInformert(fordeling.isErAnnenForelderInformert());
    }

    private static List<LukketPeriode> perioderFra(List<LukketPeriodeMedVedlegg> perioder) {
        return perioder.stream().map(FPFordelSøknadGenerator::lukkerPeriodeFra).collect(toList());

    }

    private static LukketPeriode lukkerPeriodeFra(LukketPeriodeMedVedlegg periode) {
        if (periode instanceof OverføringsPeriode) {
            OverføringsPeriode overføringsPeriode = OverføringsPeriode.class.cast(periode);
            return new Overfoeringsperiode()
                    .withFom(overføringsPeriode.getFom())
                    .withTom(overføringsPeriode.getTom())
                    .withAarsak(overføringsÅrsakFra(overføringsPeriode.getÅrsak()));
        }
        if (periode instanceof OppholdsPeriode) {
            OppholdsPeriode oppholdsPeriode = OppholdsPeriode.class.cast(periode);
            return new Oppholdsperiode()
                    .withFom(oppholdsPeriode.getFom())
                    .withTom(oppholdsPeriode.getTom())
                    .withAarsak(oppholdsårsakerFra(oppholdsPeriode.getÅrsak()));

        }
        if (periode instanceof UtsettelsesPeriode) {
            UtsettelsesPeriode utsettelsesPeriode = UtsettelsesPeriode.class.cast(periode);
            return new Utsettelsesperiode()
                    .withFom(utsettelsesPeriode.getFom())
                    .withTom(utsettelsesPeriode.getTom())
                    .withAarsak(utsettelsesårsakFra(utsettelsesPeriode.getÅrsak()));

        }
        if (periode instanceof GradertUttaksPeriode) {
            GradertUttaksPeriode uttaksPeriode = GradertUttaksPeriode.class.cast(periode);
            return new Gradering()
                    .withType(uttaksperiodeTyperFra(uttaksPeriode.getUttaksperiodeType()))
                    .withOenskerSamtidigUttak(uttaksPeriode.isØnskerSamtidigUttak())
                    .withMorsAktivitetIPerioden(morsAktivitetFra(uttaksPeriode.getMorsAktivitetsType()))
                    .withFom(uttaksPeriode.getFom())
                    .withTom(uttaksPeriode.getTom())
                    .withOenskerSamtidigUttak(uttaksPeriode.isØnskerSamtidigUttak())
                    .withErArbeidstaker(uttaksPeriode.isErArbeidstaker())
                    .withArbeidtidProsent(uttaksPeriode.getArbeidstidProsent())
                    .withVirksomhetsnummer(uttaksPeriode.getVirksomhetsNummer())
                    .withArbeidsforholdSomSkalGraderes(uttaksPeriode.isArbeidsForholdSomskalGraderes());
        }
        if (periode instanceof UttaksPeriode) {
            UttaksPeriode uttaksPeriode = UttaksPeriode.class.cast(periode);
            return new Uttaksperiode()
                    .withType(uttaksperiodeTyperFra(uttaksPeriode.getUttaksperiodeType()))
                    .withOenskerSamtidigUttak(uttaksPeriode.isØnskerSamtidigUttak())
                    .withMorsAktivitetIPerioden(morsAktivitetFra(uttaksPeriode.getMorsAktivitetsType()))
                    .withFom(uttaksPeriode.getFom())
                    .withTom(uttaksPeriode.getTom());
        }
        throw new IllegalArgumentException("Vil aldri skje");
    }

    private static Uttaksperiodetyper uttaksperiodeTyperFra(StønadskontoType type) {
        if (type == null) {
            return null;
        }
        switch (type) {
        case FEDREKVOTE:
        case FELLESPERIODE:
        case FORELDREPENGER:
        case FORELDREPENGER_FØR_FØDSEL:
        case MØDREKVOTE:
            return uttaksperiodeTypeMedKodeverk(type.name());
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    private static Uttaksperiodetyper uttaksperiodeTypeMedKodeverk(String type) {
        Uttaksperiodetyper periodeType = new Uttaksperiodetyper().withKode(type);
        return periodeType.withKodeverk(periodeType.getKodeverk());
    }

    private static MorsAktivitetsTyper morsAktivitetFra(MorsAktivitet type) {
        if (type == null) {
            return null;
        }
        switch (type) {
        case SAMTIDIGUTTAK:
        case ARBEID:
        case ARBEID_OG_UTDANNING:
        case INNLAGT:
        case INTROPROG:
        case KVALPROG:
        case TRENGER_HJELP:
        case UTDANNING:
            return morsAktivitetMedKodeverk(type.name());
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }

    }

    private static MorsAktivitetsTyper morsAktivitetMedKodeverk(String aktivitet) {
        MorsAktivitetsTyper morsAktivitet = new MorsAktivitetsTyper().withKode(aktivitet);
        return morsAktivitet.withKodeverk(morsAktivitet.getKodeverk());
    }

    private static Utsettelsesaarsaker utsettelsesårsakFra(UtsettelsesÅrsak årsak) {
        if (årsak == null) {
            return null;
        }
        switch (årsak) {
        case ARBEID:
        case INSTITUSJONSOPPHOLD_BARNET:
        case INSTITUSJONSOPPHOLD_SØKER:
        case LOVBESTEMT_FERIE:
        case SYKDOM:
            return utsettelsesÅrsakMedKodeverk(årsak.name());
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    private static Utsettelsesaarsaker utsettelsesÅrsakMedKodeverk(String årsak) {
        Utsettelsesaarsaker utsettelsesÅrsak = new Utsettelsesaarsaker().withKode(årsak);
        return utsettelsesÅrsak.withKodeverk(utsettelsesÅrsak.getKodeverk());
    }

    private static Oppholdsaarsaker oppholdsårsakerFra(Oppholdsårsak årsak) {
        if (årsak == null) {
            return null;
        }
        switch (årsak) {
        case INGEN:
        case UTTAK_FELLSP_ANNEN_FORLDER:
        case UTTAK_KVOTE_ANNEN_FORLDER:
            return oppholdsÅrsakMedKodeverk(årsak.name());
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    private static Oppholdsaarsaker oppholdsÅrsakMedKodeverk(String årsak) {
        Oppholdsaarsaker oppholdsÅrsak = new Oppholdsaarsaker().withKode(årsak);
        return oppholdsÅrsak.withKodeverk(oppholdsÅrsak.getKodeverk());
    }

    private static Overfoeringsaarsaker overføringsÅrsakFra(Overføringsårsak årsak) {
        if (årsak == null) {
            return null;
        }
        switch (årsak) {
        case ALENEOMSORG:
        case IKKE_RETT_ANNEN_FORELDER:
        case INSTITUSJONSOPPHOLD_ANNEN_FORELDER:
        case SYKDOM_ANNEN_FORELDER:
            return overføringsÅrsakMedKodeverk(årsak.name());
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }

    }

    private static Overfoeringsaarsaker overføringsÅrsakMedKodeverk(String årsak) {
        Overfoeringsaarsaker overføringsÅrsak = new Overfoeringsaarsaker().withKode(årsak);
        return overføringsÅrsak.withKodeverk(overføringsÅrsak.getKodeverk());
    }

    private static Rettigheter rettighetrFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.Rettigheter rettigheter, boolean ukjentForelder) {

        if (ukjentForelder) {
            LOG.info("Annen forelder er ukjent, avleder verdier for  rettigheter");
            return new Rettigheter()
                    .withHarOmsorgForBarnetIPeriodene(true)
                    .withHarAnnenForelderRett(false)
                    .withHarAleneomsorgForBarnet(true);
        }
        if (rettigheter == null) {
            return null;
        }
        return new Rettigheter()
                .withHarOmsorgForBarnetIPeriodene(rettigheter.isHarOmsorgForBarnetIPeriodene())
                .withHarAnnenForelderRett(rettigheter.isHarAnnenForelderRett())
                .withHarAleneomsorgForBarnet(rettigheter.isHarAleneOmsorgForBarnet());
    }

    private static AnnenForelder annenForelderFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder annenForelder) {

        if (annenForelder instanceof no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder) {
            return ukjentForelder(
                    no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder.class.cast(annenForelder));
        }
        if (annenForelder instanceof no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskForelder) {
            return utenlandsForelder(UtenlandskForelder.class.cast(annenForelder));
        }
        if (annenForelder instanceof no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskForelder) {
            return norskForelder(NorskForelder.class.cast(annenForelder));
        }
        throw new IllegalArgumentException(
                "Annen forelder " + annenForelder.getClass().getSimpleName() + " er ikke støttet");
    }

    private static UkjentForelder ukjentForelder(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder annenForelder) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v1.UkjentForelder();
    }

    private static AnnenForelderUtenNorskIdent utenlandsForelder(UtenlandskForelder utenlandskForelder) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelderUtenNorskIdent()
                .withUtenlandskPersonidentifikator(utenlandskForelder.getId())
                .withLand(landFra(utenlandskForelder.getLand()));
    }

    private static AnnenForelderMedNorskIdent norskForelder(NorskForelder norskForelder) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelderMedNorskIdent()
                .withAktoerId(norskForelder.getAktørId().getId());
    }

    private static SoekersRelasjonTilBarnet relasjonFra(RelasjonTilBarnMedVedlegg relasjonTilBarn) {
        if (relasjonTilBarn instanceof Fødsel) {
            Fødsel fødsel = Fødsel.class.cast(relasjonTilBarn);
            return new Foedsel()
                    .withFoedselsdato(fødsel.getFødselsdato().get(0))
                    .withAntallBarn(fødsel.getAntallBarn());
        }
        if (relasjonTilBarn instanceof FremtidigFødsel) {
            FremtidigFødsel termin = FremtidigFødsel.class.cast(relasjonTilBarn);
            return new Termin()
                    .withAntallBarn(termin.getAntallBarn())
                    .withTermindato(termin.getTerminDato())
                    .withUtstedtdato(termin.getUtstedtDato());
        }

        throw new IllegalArgumentException(
                "Relasjon " + relasjonTilBarn.getClass().getSimpleName() + " er ikke støttet");
    }

    private static Bruker søkerFra(AktorId aktørId, Søker søker) {
        return new Bruker()
                .withAktoerId(aktørId.getId())
                .withSoeknadsrolle(rolleFra(søker.getSøknadsRolle()));
    }

    private static Brukerroller rolleFra(BrukerRolle søknadsRolle) {
        switch (søknadsRolle) {
        case MOR:
        case FAR:
        case MEDMOR:
            return brukerrolleMedKodeverk(søknadsRolle.name());
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    private static Brukerroller brukerrolleMedKodeverk(String rolle) {
        Brukerroller brukerRolle = new Brukerroller().withKode(rolle);
        return brukerRolle.withKodeverk(brukerRolle.getKodeverk());
    }

    public String toXML(Soeknad model) {
        return marshall(CONTEXT, model, false);
    }

}
