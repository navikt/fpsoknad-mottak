package no.nav.foreldrepenger.mottak.domain.felles;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.foreldrepenger.mottak.domain.ResourceHandleUtil.bytesFra;
import static org.springframework.util.StreamUtils.copyToString;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.Søker;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.domain.felles.Bankkonto;
import no.nav.foreldrepenger.common.domain.felles.DokumentType;
import no.nav.foreldrepenger.common.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.common.domain.felles.Kjønn;
import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.PåkrevdVedlegg;
import no.nav.foreldrepenger.common.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.VedleggMetaData;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.AnnenForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.ArbeidsInformasjon;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.OmsorgsOvertakelsesÅrsak;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Omsorgsovertakelse;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;

public class TestUtils {
    public static boolean hasPdfSignature(byte[] bytes) {
        return bytes[0] == 0x25 &&
                bytes[1] == 0x50 &&
                bytes[2] == 0x44 &&
                bytes[3] == 0x46;
    }

    public static Søknad engangssøknad(boolean utland) {
        return engangssøknad(utland, termin(), norskForelder());
    }

    public static Søknad engangssøknad(RelasjonTilBarn relasjon, boolean utland) {
        return engangssøknad(utland, relasjon, norskForelder());
    }

    public static Søknad engangssøknad(RelasjonTilBarn relasjon) {
        return engangssøknad(false, relasjon, norskForelder());
    }

    public static Søknad engangssøknad(Vedlegg... vedlegg) {
        return engangssøknad(true, termin(), norskForelder(), vedlegg);
    }

    public static Søknad engangssøknad(boolean utland, RelasjonTilBarn relasjon, AnnenForelder annenForelder,
            Vedlegg... vedlegg) {
        Søknad s = new Søknad(LocalDate.now(), søker(), engangstønad(utland, relasjon, annenForelder), vedlegg);
        s.setBegrunnelseForSenSøknad("Glemte hele ungen");
        s.setTilleggsopplysninger("Intet å tilføye");
        return s;
    }

    public static Engangsstønad engangstønad(boolean utland, RelasjonTilBarn relasjon,
            AnnenForelder annenForelder) {
        Engangsstønad stønad = new Engangsstønad(medlemsskap(utland), relasjon);
        stønad.setAnnenForelder(annenForelder);
        return stønad;
    }

    public static Utenlandsopphold utenlandsopphold() {
        return new Utenlandsopphold(CountryCode.SE, varighet());
    }

    public static NorskForelder norskForelder() {
        return new NorskForelder(fnr(), "Far Farsen");
    }

    public static UtenlandskForelder utenlandskForelder() {
        return new UtenlandskForelder("123456", CountryCode.SE, "Far Farsen");
    }

    public static Medlemsskap medlemsskap() {
        return medlemsskap(false);
    }

    public static Medlemsskap medlemsskap(boolean utland) {
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
        return new TidligereOppholdsInformasjon(ArbeidsInformasjon.ARBEIDET_I_NORGE, emptyList());
    }

    public static Omsorgsovertakelse omsorgsovertakelse() {
        Omsorgsovertakelse overtakelse = new Omsorgsovertakelse(nå(), OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE,
                forrigeMåned());
        overtakelse.setBeskrivelse("dette er en beskrivelse");
        return overtakelse;
    }

    public static PåkrevdVedlegg påkrevdVedlegg(String id) {
        return påkrevdVedlegg(id, "pdf/terminbekreftelse.pdf");
    }

    public static ValgfrittVedlegg valgfrittVedlegg(String id, InnsendingsType type) {
        return valgfrittVedlegg(id, type, "pdf/terminbekreftelse.pdf");
    }

    public static PåkrevdVedlegg påkrevdVedlegg(String id, String name) {
        var vedleggMetaData = new VedleggMetaData(id, InnsendingsType.LASTET_OPP, DokumentType.I000062);
        return new PåkrevdVedlegg(vedleggMetaData, bytesFra(new ClassPathResource(name)));
    }

    static ValgfrittVedlegg valgfrittVedlegg(String id, InnsendingsType type, String name) {
        var vedleggMetaData = new VedleggMetaData(id, type, DokumentType.I000062);
        return new ValgfrittVedlegg(vedleggMetaData, bytesFra(new ClassPathResource(name)));
    }

    public static Adopsjon adopsjon() {
        return new Adopsjon(1, nå(), false, false, emptyList(), nå(), listeMedNå());
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
        return new FramtidigOppholdsInformasjon(emptyList());
    }

    public static String serialize(Object obj, boolean print, ObjectMapper mapper) {
        try {
            String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            return print ? printSerialized(serialized) : serialized;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String printSerialized(String serialized) {
        return serialized;
    }

    public static Søker søker() {
        return new Søker(BrukerRolle.MOR, Målform.standard());
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

    public static List<LocalDate> listeMedNå() {
        return singletonList(nå());
    }

    static LocalDate ettÅrSiden() {
        return LocalDate.now().minus(Period.ofYears(1));
    }

    public static AktørId aktoer() {
        return new AktørId("11111111111111111");
    }

    static Fødselsnummer fnr() {
        return new Fødselsnummer("01010111111");
    }

    public static Navn navnUtenMellomnavn() {
        return new Navn("Mor", null, "Monsen", Kjønn.K);
    }

    public static AnnenForelder ukjentForelder() {
        return new UkjentForelder();
    }

    public static Person person() {
        Person person = new Person(new Fødselsnummer("010101010101"), new Navn("Mor", "Mellommor", "Morsen", Kjønn.K),
                LocalDate.now().minusYears(25), "NN",
                CountryCode.NO,
                new Bankkonto("2000.20.20000", "Store Fiskerbank"), Set.of());
        person.setAktørId(new AktørId("42"));
        return person;
    }

    public static String load(String file) throws IOException {
        return copyToString(new ClassPathResource(file).getInputStream(), defaultCharset());
    }
}
