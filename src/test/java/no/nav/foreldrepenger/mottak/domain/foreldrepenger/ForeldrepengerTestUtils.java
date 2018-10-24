package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000063;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I500002;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I500005;
import static no.nav.foreldrepenger.mottak.domain.felles.OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.medlemsskap;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.søker;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.Virksomhetstype.FISKE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

public class ForeldrepengerTestUtils {

    public static final String ID142 = "142";
    public static final String ID143 = "143";
    public static final List<Vedlegg> TO_VEDLEGG = newArrayList(
            TestUtils.valgfrittVedlegg(ID142, InnsendingsType.LASTET_OPP),
            TestUtils.valgfrittVedlegg(ID143, InnsendingsType.LASTET_OPP));
    public static final ValgfrittVedlegg V1 = opplastetVedlegg(ID142, I500002);
    public static final ValgfrittVedlegg V2 = opplastetVedlegg(ID143, I500005);

    private static final ValgfrittVedlegg IKKE_OPPLASTETV1 = ikkeOpplastet(ID143, I000063);

    public static Søknad foreldrepengeSøknad() {
        return new Søknad(LocalDateTime.now(), TestUtils.søker(), foreldrePenger(false), TO_VEDLEGG);
    }

    public static Søknad foreldrepengeSøknadUtenVedlegg() {
        return new Søknad(LocalDateTime.now(), TestUtils.søker(), foreldrePenger(false));
    }

    public static Søknad søknadMedEttVedlegg() {
        return søknad(false, V1);
    }

    public static Søknad søknadMedEttOpplastetEttIkkeOpplastetVedlegg() {
        return søknad(false, V1, IKKE_OPPLASTETV1);
    }

    public static Søknad søknadMedToVedlegg() {
        return søknad(false, V1, V2);
    }

    public static Søknad søknadMedEttIkkeOpplastedVedlegg(boolean utland) {
        return søknad(utland, IKKE_OPPLASTETV1);
    }

    public static Søknad søknad(boolean utland, Vedlegg... vedlegg) {
        return new Søknad(LocalDateTime.now(), TestUtils.søker(), foreldrePenger(utland), asList(vedlegg));
    }

    public static Endringssøknad endringssøknad(Vedlegg... vedlegg) {
        return new Endringssøknad(LocalDateTime.now(), søker(),
                fordeling(vedleggRefs(vedlegg)), norskForelder(),
                fødsel(),
                rettigheter(),
                "42", vedlegg);
    }

    private static List<String> vedleggRefs(Vedlegg... vedlegg) {
        return Arrays.stream(vedlegg)
                .map(s -> s.getId())
                .collect(Collectors.toList());
    }

    public static Ettersending ettersending() {
        return new Ettersending("42", TO_VEDLEGG);
    }

    static Foreldrepenger foreldrePenger(boolean utland) {
        return Foreldrepenger.builder()
                .rettigheter(rettigheter())
                .annenForelder(norskForelder())
                .dekningsgrad(Dekningsgrad.GRAD100)
                .fordeling(fordeling(Collections.emptyList()))
                .opptjening(opptjening())
                .relasjonTilBarn(termin())
                .medlemsskap(medlemsskap(utland))
                .build();
    }

    static Opptjening opptjening() {
        return new Opptjening(Collections.singletonList(utenlandskArbeidsforhold()), egneNæringer(),
                andreOpptjeninger(), frilans());
    }

    private static Frilans frilans() {
        return new Frilans(åpenPeriode(true), true, true,
                newArrayList(new FrilansOppdrag("bror min", åpenPeriode(true)),
                        new FrilansOppdrag("far min", åpenPeriode(true))),
                newArrayList(ID142, ID143));

    }

    private static List<AnnenOpptjening> andreOpptjeninger() {
        return newArrayList(annenOpptjening());
    }

    private static List<EgenNæring> egneNæringer() {
        return newArrayList(utenlandskEgenNæring(), norskEgenNæring());
    }

    static UtenlandskForelder utenlandskForelder() {
        return new UtenlandskForelder("42", CountryCode.SE, "Pedro Bandolero");
    }

    static NorskForelder norskForelder() {
        return new NorskForelder(new Fødselsnummer("01010111111"), "Jan Johansen");
    }

    static Adopsjon adopsjon() {
        return new Adopsjon(0, LocalDate.now(), true, null, LocalDate.now(),
                Collections.singletonList(LocalDate.now()));
    }

    static ÅpenPeriode åpenPeriode() {
        return åpenPeriode(false);
    }

    static ÅpenPeriode åpenPeriode(boolean end) {

        return end ? new ÅpenPeriode(LocalDate.now().minusMonths(5), LocalDate.now())
                : new ÅpenPeriode(LocalDate.now().minusMonths(5));
    }

    static Omsorgsovertakelse omsorgsovertakelse() {
        return new Omsorgsovertakelse(LocalDate.now(), SKAL_OVERTA_ALENE, LocalDate.now());
    }

    static UtenlandskOrganisasjon utenlandskEgenNæring() {
        return UtenlandskOrganisasjon.builder()
                .periode(åpenPeriode())
                .regnskapsførere(Collections.singletonList(new Regnskapsfører("Rein S. Kapsfører", "+4746929061")))
                .erNyOpprettet(true)
                .erVarigEndring(true)
                .erNyIArbeidslivet(false)
                .næringsinntektBrutto(100_000)
                .orgName("Utenlandsk org")
                .virksomhetsTyper(Collections.singletonList(FISKE))
                .arbeidsland(CountryCode.SE)
                .beskrivelseEndring("Endringer skjer fort i verdens største land (utlandet)")
                .nærRelasjon(true)
                .endringsDato(LocalDate.now()).build();
    }

    static NorskOrganisasjon norskEgenNæring() {
        return NorskOrganisasjon.builder()
                .periode(åpenPeriode())
                .regnskapsførere(Collections.singletonList(new Regnskapsfører("Rein S. Kapsfører", "+4746929061")))
                .erNyOpprettet(true)
                .erVarigEndring(true)
                .erNyIArbeidslivet(true)
                .erNyOpprettet(true)
                .næringsinntektBrutto(100_000)
                .orgName("Norsk org")
                .orgNummer("82828282")
                .virksomhetsTyper(Collections.singletonList(FISKE))
                .arbeidsland(CountryCode.NO)
                .beskrivelseEndring("Ting endrer seg i Norge også")
                .nærRelasjon(true)
                .endringsDato(LocalDate.now()).build();
    }

    static AnnenOpptjening annenOpptjening() {
        return new AnnenOpptjening(AnnenOpptjeningType.LØNN_UNDER_UTDANNING, åpenPeriode(), null);
    }

    static UtenlandskArbeidsforhold utenlandskArbeidsforhold(Vedlegg... vedlegg) {
        return UtenlandskArbeidsforhold.builder()
                .vedlegg(vedleggRefs(vedlegg))
                .arbeidsgiverNavn("boss")
                .land(CountryCode.PL)
                .periode(åpenPeriode()).build();
    }

    private static List<LukketPeriodeMedVedlegg> perioder(List<String> vedlegg) {
        return newArrayList(
                oppholdsPeriode(vedlegg),
                overføringsPeriode(vedlegg),
                utsettelsesPeriode(vedlegg),
                uttaksPeriode(vedlegg),
                gradertPeriode(vedlegg));
    }

    static UttaksPeriode uttaksPeriode(List<String> vedlegg) {
        return new UttaksPeriode(LocalDate.now().minusMonths(1), LocalDate.now(), FEDREKVOTE,
                true, MorsAktivitet.ARBEID_OG_UTDANNING, true, 75.0d, vedlegg);
    }

    static UttaksPeriode gradertPeriode(List<String> vedlegg) {
        return new GradertUttaksPeriode(LocalDate.now().minusMonths(1), LocalDate.now(),
                FEDREKVOTE,
                true, MorsAktivitet.ARBEID_OG_UTDANNING, true, 42d, 75d, true, true, "222222", vedlegg);
    }

    static FremtidigFødsel termin() {
        return new FremtidigFødsel(LocalDate.now(), LocalDate.now());
    }

    static Fødsel fødsel() {
        return new Fødsel(LocalDate.now().minusMonths(2));
    }

    static OverføringsPeriode overføringsPeriode(List<String> vedlegg) {
        return new OverføringsPeriode(LocalDate.now().minusMonths(1), LocalDate.now(),
                Overføringsårsak.ALENEOMSORG, StønadskontoType.FEDREKVOTE, vedlegg);
    }

    static OppholdsPeriode oppholdsPeriode(List<String> vedlegg) {
        return new OppholdsPeriode(LocalDate.now().minusMonths(1), LocalDate.now(),
                Oppholdsårsak.UTTAK_FEDREKVOTE_ANNEN_FORELDER,
                vedlegg);
    }

    static UtsettelsesPeriode utsettelsesPeriode(List<String> vedlegg) {
        return new UtsettelsesPeriode(LocalDate.now().minusMonths(1), LocalDate.now(), true, "222",
                UtsettelsesÅrsak.INSTITUSJONSOPPHOLD_BARNET, StønadskontoType.FEDREKVOTE, vedlegg);
    }

    static Fordeling fordeling(List<String> vedlegg) {
        return new Fordeling(true, Overføringsårsak.IKKE_RETT_ANNEN_FORELDER, perioder(vedlegg));
    }

    static Rettigheter rettigheter() {
        return new Rettigheter(true, true, true, LocalDate.now());
    }

    private static ValgfrittVedlegg opplastetVedlegg(String id, DokumentType type) {
        try {
            return new ValgfrittVedlegg(id, InnsendingsType.LASTET_OPP, type,
                    new ClassPathResource("terminbekreftelse.pdf"));
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private static ValgfrittVedlegg ikkeOpplastet(String id, DokumentType type) {
        try {
            return new ValgfrittVedlegg(id, InnsendingsType.SEND_SENERE, type, null);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }
}
