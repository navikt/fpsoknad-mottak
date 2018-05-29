package no.nav.foreldrepenger.mottak.fpfordel;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.Jaxb.marshall;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;

import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenOpptjeningType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ArbeidsforholdType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.EgenNæring;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.MorsAktivitetstype;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskArbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OppholdsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Oppholdsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OverføringsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Overføringsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.RelasjonTilBarnMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtsettelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UttaksperiodeType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ÅpenPeriode;
import no.nav.foreldrepenger.mottak.util.Jaxb;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Brukerroller;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Foedsel;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Innsendingstype;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Land;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.LukketPeriode;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Periode;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Rettigheter;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.SoekersRelasjonTilBarnet;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Termin;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Vedlegg;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Ytelse;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.AnnenOpptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.AnnenOpptjeningTyper;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Arbeidsforhold;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Arbeidsforholdtyper;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Dekningsgrad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Dekningsgrader;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.EgenNaering;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.MorsAktivitetsTyper;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.NorskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.UtenlandskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Oppholdsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Oppholdsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Overfoeringsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Overfoeringsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Utsettelsesaarsaker;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Utsettelsesperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Uttaksperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Uttaksperiodetyper;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

@Component
public class FPFordelSøknadGenerator {
    private static final JAXBContext CONTEXT = Jaxb.context(Soeknad.class);

    public String toXML(Søknad søknad, AktorId aktørId) {
        return toXML(toFPFordelModel(søknad, aktørId));
    }

    public Soeknad toFPFordelModel(Søknad søknad, AktorId aktørId) {
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
                .withInnsendingstype(new Innsendingstype().withKode("LASTET_OPP"));
    }

    private static Ytelse ytelseFra(Søknad søknad) {
        no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger ytelse = no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.class
                .cast(søknad.getYtelse());
        return new Foreldrepenger()
                .withDekningsgrad(dekningsgradFra(ytelse.getDekningsgrad()))
                .withMedlemskap(medlemsskapFra(ytelse.getMedlemsskap()))
                .withOpptjening(opptjeningFra(ytelse.getOpptjening()))
                .withFordeling(fordelingFra(ytelse.getFordeling()))
                .withRettigheter(rettighetrFra(ytelse.getRettigheter()))
                .withAnnenForelder(annenForelderFra(ytelse.getAnnenForelder()))
                .withRelasjonTilBarnet(relasjonFra(ytelse.getRelasjonTilBarn()));
    }

    private static Dekningsgrad dekningsgradFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad dekningsgrad) {
        return new Dekningsgrad()
                .withDekningsgrad(new Dekningsgrader()
                        .withKode(dekningsgrad.kode()));
    }

    private static Opptjening opptjeningFra(no.nav.foreldrepenger.mottak.domain.foreldrepenger.Opptjening opptjening) {
        return new Opptjening()
                .withEgenNaering(egenNæringFra(opptjening.getEgenNæring()))
                .withArbeidsforhold(arbeidForholdFra(opptjening.getArbeidsforhold()))
                .withAnnenOpptjening(annenOpptjeningFra(opptjening.getAnnenOpptjening()));
    }

    private static List<EgenNaering> egenNæringFra(List<EgenNæring> egenNæring) {
        return egenNæring.stream()
                .map(FPFordelSøknadGenerator::egenNæringFra)
                .collect(toList());
    }

    private static List<Arbeidsforhold> arbeidForholdFra(
            List<no.nav.foreldrepenger.mottak.domain.foreldrepenger.Arbeidsforhold> arbeidsforhold) {
        return arbeidsforhold.stream()
                .map(FPFordelSøknadGenerator::arbeidsforholdFra)
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
            return new NorskOrganisasjon();
        }
        if (egenNæring instanceof no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskOrganisasjon) {
            return new UtenlandskOrganisasjon();
        }
        throw new IllegalArgumentException("Vil aldri skje");

    }

    private static AnnenOpptjening annenOpptjeningFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenOpptjening annenOpptjening) {
        return new AnnenOpptjening()
                .withType(new AnnenOpptjeningTyper().withKode(opptjeningtyperFra(annenOpptjening.getType())))
                .withPeriode(periodeFra(annenOpptjening.getPeriode()));
    }

    private static Arbeidsforhold arbeidsforholdFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.Arbeidsforhold arbeidsForhold) {
        if (arbeidsForhold instanceof NorskArbeidsforhold) {
            return norskArbeidsforhold(NorskArbeidsforhold.class.cast(arbeidsForhold));
        }
        if (arbeidsForhold instanceof UtenlandskArbeidsforhold) {
            return utenlandskArbeidsforhold(UtenlandskArbeidsforhold.class.cast(arbeidsForhold));
        }
        throw new IllegalArgumentException("Vil aldri skje");

    }

    private static no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.NorskArbeidsforhold norskArbeidsforhold(
            NorskArbeidsforhold arbeidsForhold) {
        return new no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.NorskArbeidsforhold()
                .withVirksomhetsnummer(arbeidsForhold.getOrgNummer())
                .withBeskrivelseAvNaerRelasjon(arbeidsForhold.getBeskrivelseRelasjon())
                .withArbeidsgiversnavn(arbeidsForhold.getArbeidsgiverNavn())
                .withArbeidsforholdtype(
                        new Arbeidsforholdtyper()
                                .withKode(arbeidsforholdtypeFra(arbeidsForhold.getType())))
                .withPeriode(periodeFra(arbeidsForhold.getPeriode()));
    }

    private static String arbeidsforholdtypeFra(ArbeidsforholdType type) {
        switch (type) {
        case ARBEIDSTAKER_PÅ_OPPDRAG:
        case FRILANSER_OPPDRAGSTAKER:
        case MARITIMT:
        case ORDINÆRT:
            return type.name();
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    private static no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.UtenlandskArbeidsforhold utenlandskArbeidsforhold(
            UtenlandskArbeidsforhold arbeidsForhold) {
        return new no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.UtenlandskArbeidsforhold()
                .withArbeidsgiversnavn(arbeidsForhold.getArbeidsgiverNavn())
                .withArbeidsland(new Land().withKode(landkodeFra(arbeidsForhold.getLand())))
                .withBeskrivelseAvNaerRelasjon(arbeidsForhold.getBeskrivelseRelasjon())
                .withHarHattInntektIPerioden(arbeidsForhold.isHarHattArbeidIPerioden())
                .withPeriode(periodeFra(arbeidsForhold.getPeriode()));
    }

    private static String landkodeFra(CountryCode land) {
        return land != null ? land.getAlpha3() : null;
    }

    private static String opptjeningtyperFra(AnnenOpptjeningType type) {
        switch (type) {
        case ETTERLØNN:
        case LØNN_UNDER_UTDANNING:
        case MILITÆR_ELLER_SIVILTJENESTE:
        case SLUTTPAKKE:
        case VARTPENGER:
        case VENTELØNN:
            return type.name();
        default:
            throw new IllegalArgumentException("Vil aldri skje");

        }

    }

    private static Periode periodeFra(ÅpenPeriode periode) {
        return new Periode().withFom(periode.getFom());
    }

    private static Medlemskap medlemsskapFra(Medlemsskap medlemsskap) {
        return new Medlemskap();
    }

    private static Fordeling fordelingFra(no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fordeling fordeling) {
        return new Fordeling()
                .withPerioder(perioderFra(fordeling.getPerioder()))
                .withOenskerKvoteOverfoert(overføringsÅrsakFra(fordeling.getØnskerKvoteOverført()))
                .withAnnenForelderErInformert(fordeling.isErAnnenForelderInformert());
    }

    private static List<LukketPeriode> perioderFra(List<LukketPeriodeMedVedlegg> perioder) {
        return perioder.stream().map(FPFordelSøknadGenerator::lukkerPeriodeFra).collect(Collectors.toList());

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
        if (periode instanceof UttaksPeriode) {
            UttaksPeriode uttaksPeriode = UttaksPeriode.class.cast(periode);
            return new Uttaksperiode()
                    .withType(new Uttaksperiodetyper()
                            .withKode(uttaksperiodeTyperFra(uttaksPeriode.getUttaksperiodeType())))
                    .withOenskerSamtidigUttak(uttaksPeriode.isØnskerSamtidigUttak())
                    .withMorsAktivitetIPerioden(
                            new MorsAktivitetsTyper()
                                    .withKode(morsAktivitetFra(uttaksPeriode.getMorsAktivitetsType())))
                    .withFom(uttaksPeriode.getFom())
                    .withTom(uttaksPeriode.getTom());
        }
        throw new IllegalArgumentException("Vil aldri skje");
    }

    private static String uttaksperiodeTyperFra(UttaksperiodeType type) {
        switch (type) {
        case FEDREKVOTE:
        case FELLESPERIODE:
        case FORELDREPENGER:
        case FORELDREPENGER_FØR_FØDSEL:
        case MØDREKVOTE:
            return type.name();
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    private static String morsAktivitetFra(MorsAktivitetstype type) {
        switch (type) {
        case ARBEID:
        case ARBEID_OG_UTDANNING:
        case INNLAGT:
        case INTROPROG:
        case KVALPROG:
        case TRENGER_HJELP:
        case UTDANNING:
            return type.name();
        default:
            throw new IllegalArgumentException("Vil aldri skje");

        }

    }

    private static Utsettelsesaarsaker utsettelsesårsakFra(UtsettelsesÅrsak årsak) {
        switch (årsak) {
        case ARBEID:
            return new Utsettelsesaarsaker().withKode("ARBEID");
        case INSTITUSJONSOPPHOLD_BARN:
            return new Utsettelsesaarsaker().withKode("INSTITUSJONSOPPHOLD_BARN");
        case INSTITUSJONSOPPHOLD_SØKER:
            return new Utsettelsesaarsaker().withKode("INSTITUSJONSOPPHOLD_SØKER");
        case LOVBESTEMT_FERIE:
            return new Utsettelsesaarsaker().withKode("LOVBESTEMT_FERIE");
        case SYKDOM:
            return new Utsettelsesaarsaker().withKode("SYKDOM");
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    private static Oppholdsaarsaker oppholdsårsakerFra(Oppholdsårsak årsak) {
        switch (årsak) {
        case INGEN:
            return new Oppholdsaarsaker().withKode("INGEN");
        case UTTAK_FELLSP_ANNEN_FORLDER:
            return new Oppholdsaarsaker().withKode("UTTAK_FELLSP_ANNEN_FORLDER");
        case UTTAK_KVOTE_ANNEN_FORLDER:
            return new Oppholdsaarsaker().withKode("UTTAK_KVOTE_ANNEN_FORLDER");
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    private static Overfoeringsaarsaker overføringsÅrsakFra(Overføringsårsak ønskerKvoteOverført) {
        switch (ønskerKvoteOverført) {
        case ALENEOMSORG:
            return new Overfoeringsaarsaker().withKode("ALENEOMSORG");
        case IKKE_RETT_ANNEN_FORELDER:
            return new Overfoeringsaarsaker().withKode("IKKE_RETT_ANNEN_FORELDER");
        case INSTITUSJONSOPPHOLD_ANNEN_FORELDER:
            return new Overfoeringsaarsaker().withKode("INSTITUSJONSOPPHOLD_ANNEN_FORELDER");
        case SYKDOM_ANNEN_FORELDER:
            return new Overfoeringsaarsaker().withKode("SYKDOM_ANNEN_FORELDER");
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }

    }

    private static Rettigheter rettighetrFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.Rettigheter rettigheter) {
        return new Rettigheter()
                .withHarOmsorgForBarnetIPeriodene(rettigheter.isHarOmsorgForBarnetIPeriodene())
                .withHarAnnenForelderRett(rettigheter.isHarAnnenForelderRett())
                .withHarAleneomsorgForBarnet(rettigheter.isHarAleneOmsorgForBarnet());
    }

    private static AnnenForelder annenForelderFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder annenForelder) {

        if (annenForelder instanceof no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder) {
            return new no.nav.vedtak.felles.xml.soeknad.felles.v1.UkjentForelder();
        }
        throw new IllegalArgumentException(
                "Annen forelder " + annenForelder.getClass().getSimpleName() + " er ikke støttet");
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
            return new Brukerroller().withKode("MOR");
        case FAR:
            return new Brukerroller().withKode("FAR");
        case MEDMOR:
            return new Brukerroller().withKode("MEDMOR");
        case ANDRE:
            return new Brukerroller().withKode("ANDRE");
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    public String toXML(Soeknad model) {
        return marshall(CONTEXT, model, false);
    }

}
