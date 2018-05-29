package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.google.inject.internal.util.Lists.newArrayList;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.medlemsskap;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.navnUtenMellomnavn;
import static no.nav.foreldrepenger.mottak.domain.felles.OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.UttaksperiodeType.FEDREKVOTE;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.Virksomhetstype.FISKE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.TestUtils;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

public class ForeldrepengerTestUtils {

    private static final List<Vedlegg> INGEN = Collections.emptyList();
    public static final List<Vedlegg> ETT_VEDLEGG = Collections.singletonList(TestUtils.valgfrittVedlegg());

    private static final List<LukketPeriodeMedVedlegg> PERIODER = perioder();

    public static Søknad søknad() {
        return new Søknad(LocalDateTime.now(), TestUtils.søker(), foreldrePenger(), ETT_VEDLEGG);
    }

    /*
     *
     * public static Foreldrepenger foreldrepenger() { return
     * Foreldrepenger.builder() .annenForelder(norskFPForelder())
     * .relasjonTilBarn(relasjonTilBarnFP())
     * .dekningsgrad(Dekningsgrad.GRAD100).build(); }
     *
     * private static RelasjonTilBarnMedVedlegg relasjonTilBarnFP() { return new
     * Fødsel(1, LocalDate.now()); }
     *
     * private static
     * no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder
     * norskFPForelder() { return new
     * no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskForelder(new
     * AktorId("42")); }
     *
     * public static Søknad foreldrepengerSøknad() { Søknad s = new
     * Søknad(LocalDateTime.now(), søker(), foreldrepenger(),
     * ForeldrepengerTestUtils.ETT_VEDLEGG);
     * s.setBegrunnelseForSenSøknad("Glemte hele ungen");
     * s.setTilleggsopplysninger("Intet å tilføye");
     *
     * return s; }
     */

    static Foreldrepenger foreldrePenger() {
        return Foreldrepenger.builder()
                .rettigheter(rettigheter())
                .annenForelder(norskForelder())
                .dekningsgrad(Dekningsgrad.GRAD100)
                .fordeling(fordeling())
                .opptjening(opptjening())
                .relasjonTilBarn(termin())
                .medlemsskap(medlemsskap(true))
                .build();
    }

    static Opptjening opptjening() {
        return new Opptjening(arbeidsforhold(), egneNæringer(), andreOpptjeninger());
    }

    static List<AnnenOpptjening> andreOpptjeninger() {
        return newArrayList(annenOpptjening());
    }

    static List<EgenNæring> egneNæringer() {
        return newArrayList(utenlandskEgenNæring(), norskEgenNæring());

    }

    static List<Arbeidsforhold> arbeidsforhold() {
        return newArrayList(norskArbeidsforhold(), utenlandskArbeidsforhold());
    }

    static UtenlandskForelder utenlandskForelder() {
        return new UtenlandskForelder("42", CountryCode.SE);
    }

    static NorskForelder norskForelder() {
        return new NorskForelder(new AktorId("42"));
    }

    static Adopsjon adopsjon() {
        return new Adopsjon(LocalDate.now(), true, LocalDate.now());
    }

    static ÅpenPeriode åpenPeriode() {
        return new ÅpenPeriode(LocalDate.now());
    }

    static Omsorgsovertakelse omsorgsovertakelse() {
        return new Omsorgsovertakelse(LocalDate.now(), SKAL_OVERTA_ALENE, LocalDate.now());
    }

    static NorskArbeidsforhold norskArbeidsforhold() {
        return NorskArbeidsforhold.builder()
                .arbeidsgiverNavn("boss")
                .bekreftelseRelasjon("relasjon")
                .type(ArbeidsforholdType.ORDINÆRT)
                .orgNummer("222222222")
                .periode(åpenPeriode()).build();
    }

    static UtenlandskOrganisasjon utenlandskEgenNæring() {
        return UtenlandskOrganisasjon.builder()
                .periode(åpenPeriode())
                .regnskapsfører(new Regnskapsfører(navnUtenMellomnavn(), "+4746929061"))
                .erNyOpprettet(true)
                .erVarigEndring(true)
                .næringsinntektBrutto(100_000)
                .orgName("My org")
                .registrertLand(CountryCode.SE)
                .stillingsprosent(42)
                .virksomhetsType(FISKE)
                .arbeidsland(CountryCode.SE).beskrivelseEndring("Stor endring")
                .beskrivelseRelasjon("relasjon")
                .endringsDato(LocalDate.now()).build();
    }

    static NorskOrganisasjon norskEgenNæring() {
        return NorskOrganisasjon.builder()
                .periode(åpenPeriode())
                .regnskapsfører(new Regnskapsfører(navnUtenMellomnavn(), "+4746929061"))
                .erNyOpprettet(true)
                .erVarigEndring(true)
                .næringsinntektBrutto(100_000)
                .orgName("My org")
                .orgNummer("82828282")
                .virksomhetsType(FISKE)
                .arbeidsland(CountryCode.SE).beskrivelseEndring("Stor endring")
                .beskrivelseRelasjon("relasjon")
                .endringsDato(LocalDate.now()).build();
    }

    static AnnenOpptjening annenOpptjening() {
        return new AnnenOpptjening(AnnenOpptjeningType.SLUTTPAKKE, åpenPeriode(), null);
    }

    static UtenlandskArbeidsforhold utenlandskArbeidsforhold() {
        return UtenlandskArbeidsforhold.builder()
                .arbeidsgiverNavn("boss")
                .bekreftelseRelasjon("relasjon")
                .harHattArbeidIPerioden(true)
                .land(CountryCode.PL)
                .periode(åpenPeriode()).build();
    }

    static List<LukketPeriodeMedVedlegg> perioder() {
        return newArrayList(oppholdsPeriode(), overføringsPeriode(), utsettelsesPeriode(), gradertPeriode());
    }

    static UttaksPeriode uttaksPeriode() {
        return new UttaksPeriode(LocalDate.now().minusMonths(1), LocalDate.now(), ETT_VEDLEGG,
                FEDREKVOTE, true, MorsAktivitetstype.ARBEID_OG_UTDANNING);
    }

    static UttaksPeriode gradertPeriode() {
        GradertUttaksPeriode periode = new GradertUttaksPeriode(LocalDate.now().minusMonths(1), LocalDate.now(),
                ETT_VEDLEGG,
                FEDREKVOTE, true, MorsAktivitetstype.ARBEID_OG_UTDANNING);
        periode.setArbeidsForholdSomskalGraderes(true);
        periode.setArbeidstidProsent(75d);
        periode.setErArbeidstaker(true);
        periode.setVirksomhetsNummer("222222");
        return periode;
    }

    static FremtidigFødsel termin() {
        return new FremtidigFødsel(LocalDate.now(), LocalDate.now());
    }

    static OverføringsPeriode overføringsPeriode() {
        return new OverføringsPeriode(LocalDate.now().minusMonths(1), LocalDate.now(), ETT_VEDLEGG,
                Overføringsårsak.ALENEOMSORG);
    }

    static OppholdsPeriode oppholdsPeriode() {
        return new OppholdsPeriode(LocalDate.now().minusMonths(1), LocalDate.now(), ETT_VEDLEGG,
                Oppholdsårsak.UTTAK_FELLSP_ANNEN_FORLDER);
    }

    static UtsettelsesPeriode utsettelsesPeriode() {
        return new UtsettelsesPeriode(LocalDate.now().minusMonths(1), LocalDate.now(), ETT_VEDLEGG,
                UtsettelsesÅrsak.INSTITUSJONSOPPHOLD_BARN);
    }

    static Fordeling fordeling() {
        return new Fordeling(true, Overføringsårsak.IKKE_RETT_ANNEN_FORELDER, PERIODER);
    }

    static Rettigheter rettigheter() {
        return new Rettigheter(true, true, true);
    }
}
