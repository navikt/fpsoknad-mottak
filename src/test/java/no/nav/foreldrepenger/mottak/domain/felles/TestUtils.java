package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class TestUtils {

    public static boolean hasPdfSignature(byte[] bytes) {
        return bytes[0] == 0x25 &&
                bytes[1] == 0x50 &&
                bytes[2] == 0x44 &&
                bytes[3] == 0x46;
    }

    public static Søknad engangssøknad(Versjon v, boolean utland) {
        return engangssøknad(v, utland, termin(), norskForelder(v));
    }

    public static Søknad engangssøknad(Versjon v, RelasjonTilBarn relasjon, boolean utland) {
        return engangssøknad(v, utland, relasjon, norskForelder(v));
    }

    public static Søknad engangssøknad(Versjon v, RelasjonTilBarn relasjon) {
        return engangssøknad(v, false, relasjon, norskForelder(v));
    }

    public static Søknad engangssøknad(Versjon v, Vedlegg... vedlegg) {
        return engangssøknad(v, true, termin(), norskForelder(v), vedlegg);
    }

    public static Søknad engangssøknad(Versjon v, boolean utland, RelasjonTilBarn relasjon, AnnenForelder annenForelder,
            Vedlegg... vedlegg) {
        Søknad s = new Søknad(LocalDateTime.now(), søker(), engangstønad(v, utland, relasjon, annenForelder), vedlegg);
        s.setBegrunnelseForSenSøknad("Glemte hele ungen");
        s.setTilleggsopplysninger("Intet å tilføye");
        return s;
    }

    public static Engangsstønad engangstønad(Versjon v, boolean utland, RelasjonTilBarn relasjon,
            AnnenForelder annenForelder) {
        Engangsstønad stønad = new Engangsstønad(medlemsskap(v, utland), relasjon);
        stønad.setAnnenForelder(annenForelder);
        return stønad;
    }

    public static Utenlandsopphold utenlandsopphold() {
        return new Utenlandsopphold(CountryCode.SE, varighet());
    }

    public static NorskForelder norskForelder(Versjon v) {
        return new NorskForelder(fnr(), "Far Farsen");
    }

    public static UtenlandskForelder utenlandskForelder() {
        return new UtenlandskForelder("123456", CountryCode.SE, "Far Farsen");
    }

    public static Medlemsskap medlemsskap(Versjon v) {
        return medlemsskap(v, false);
    }

    public static Medlemsskap medlemsskap(Versjon v, boolean utland) {
        if (utland) {
            return new Medlemsskap(tidligereOppHoldIUtlandet(), framtidigOppHoldIUtlandet());
        }
        return new Medlemsskap(tidligereOppHoldINorge(), framtidigOppholdINorge());
    }

    static TidligereOppholdsInformasjon tidligereOppHoldIUtlandet() {
        List<Utenlandsopphold> utenlandOpphold = new ArrayList<>();
        utenlandOpphold.add(new Utenlandsopphold(CountryCode.AT,
                new LukketPeriode(LocalDate.now().minusYears(1), LocalDate.now().minusMonths(6).minusDays(1))));
        utenlandOpphold.add(new Utenlandsopphold(CountryCode.FI,
                new LukketPeriode(LocalDate.now().minusMonths(6), LocalDate.now())));
        return new TidligereOppholdsInformasjon(ArbeidsInformasjon.ARBEIDET_I_UTLANDET, utenlandOpphold);
    }

    static TidligereOppholdsInformasjon tidligereOppHoldINorge() {
        return new TidligereOppholdsInformasjon(ArbeidsInformasjon.ARBEIDET_I_NORGE, Collections.emptyList());
    }

    public static Omsorgsovertakelse omsorgsovertakelse() {
        Omsorgsovertakelse overtakelse = new Omsorgsovertakelse(nå(), OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE,
                forrigeMåned());
        overtakelse.setBeskrivelse("dette er en beskrivelse");
        return overtakelse;
    }

    public static PåkrevdVedlegg påkrevdVedlegg(String id) {
        return påkrevdVedlegg(id, "terminbekreftelse.pdf");
    }

    public static ValgfrittVedlegg valgfrittVedlegg(String id, InnsendingsType type) {
        return valgfrittVedlegg(id, type, "terminbekreftelse.pdf");
    }

    public static PåkrevdVedlegg påkrevdVedlegg(String id, String name) {
        return new PåkrevdVedlegg(id, DokumentType.I000062, new ClassPathResource(name));
    }

    static ValgfrittVedlegg valgfrittVedlegg(String id, InnsendingsType type, String name) {
        return new ValgfrittVedlegg(id, type, DokumentType.I000062,
                new ClassPathResource(name));
    }

    public static Adopsjon adopsjon() {
        return new Adopsjon(nå(), false, 1, nå(), nå());
    }

    public static RelasjonTilBarn fødsel() {
        return fødsel(forrigeMåned());
    }

    public static RelasjonTilBarn fødsel(LocalDate date) {
        return new Fødsel(date);
    }

    public static FramtidigOppholdsInformasjon framtidigOppHoldIUtlandet() {
        List<Utenlandsopphold> opphold = new ArrayList<>();
        opphold.add(new Utenlandsopphold(CountryCode.GR,
                new LukketPeriode(LocalDate.now(), LocalDate.now().plusMonths(6))));
        opphold.add(new Utenlandsopphold(CountryCode.GR,
                new LukketPeriode(LocalDate.now(), LocalDate.now().plusMonths(6))));
        opphold.add(new Utenlandsopphold(CountryCode.GR,
                new LukketPeriode(LocalDate.now(), LocalDate.now().plusMonths(6))));
        opphold.add(new Utenlandsopphold(CountryCode.GR,
                new LukketPeriode(LocalDate.now(), LocalDate.now().plusMonths(6))));
        opphold.add(new Utenlandsopphold(CountryCode.DE,
                new LukketPeriode(LocalDate.now().plusMonths(6).plusDays(1), LocalDate.now().plusYears(1))));
        return new FramtidigOppholdsInformasjon(opphold);
    }

    public static FramtidigOppholdsInformasjon framtidigOppholdINorge() {
        return new FramtidigOppholdsInformasjon(Collections.emptyList());
    }

    public static String serialize(Object obj, boolean print, ObjectMapper mapper) throws JsonProcessingException {
        String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        return print ? printSerialized(serialized) : serialized;
    }

    static String printSerialized(String serialized) {
        return serialized;
    }

    public static Søker søker() {
        return søker(navn());
    }

    public static Søker søker(Navn navn) {
        return new Søker(BrukerRolle.MOR);
    }

    private static Navn navn() {
        return new Navn("Mor", "Godhjerta", "Morsen");
    }

    public static RelasjonTilBarn termin() {
        return new FremtidigFødsel(nesteMåned(), forrigeMåned());
    }

    public static LukketPeriode varighet() {
        return new LukketPeriode(nå(), nesteMåned());
    }

    public static LocalDate nesteMåned() {
        return nå().plus(enMåned());
    }

    static LocalDate forrigeMåned() {
        return nå().minusMonths(1);
    }

    static Period enMåned() {
        return måned(1);
    }

    static Period måned(int n) {
        return Period.ofMonths(n);
    }

    public static LocalDate nå() {
        return LocalDate.now();
    }

    static LocalDate ettÅrSiden() {
        return LocalDate.now().minus(Period.ofYears(1));
    }

    public static AktorId aktoer() {
        return new AktorId("11111111111111111");
    }

    static Fødselsnummer fnr() {
        return new Fødselsnummer("01010111111");
    }

    public static Navn navnUtenMellomnavn() {
        return new Navn("Mor", null, "Monsen");
    }

    public static AnnenForelder ukjentForelder() {
        return new UkjentForelder();
    }

    public static Person person() {
        Person søker = new Person();
        søker.aktørId = new AktorId("42");
        søker.bankkonto = new Bankkonto("2000.20.20000", "Store Fiskerbank");
        søker.fnr = new Fødselsnummer("010101010101");
        søker.fornavn = "Åse";
        søker.mellomnavn = "Mañana";
        søker.etternavn = "Pålsen";
        søker.fødselsdato = LocalDate.now().minusYears(25);
        søker.kjønn = "K";
        søker.ikkeNordiskEøsLand = false;
        søker.land = CountryCode.NO;
        søker.målform = "NN";
        return søker;
    }
}
