package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.domain.felles.VedleggSkjemanummer.TERMINBEKREFTELSE;

import java.io.IOException;
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

import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.felles.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.felles.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.Fødsel;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.OmsorgsOvertakelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.felles.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.felles.PåkrevdVedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.domain.felles.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;

public class TestUtils {

    public static boolean hasPdfSignature(byte[] bytes) {
        return bytes[0] == 0x25 &&
                bytes[1] == 0x50 &&
                bytes[2] == 0x44 &&
                bytes[3] == 0x46;
    }

    public static Foreldrepenger foreldrepenger() {
        return Foreldrepenger.builder().dekningsgrad(Dekningsgrad.GRAD100).build();
    }

    public static Søknad foreldrepengerSøknad() {
        Søknad s = new Søknad(LocalDateTime.now(), søker(), foreldrepenger());
        s.setBegrunnelseForSenSøknad("Glemte hele ungen");
        s.setTilleggsopplysninger("Intet å tilføye");
        return s;
    }

    public static Søknad engangssøknad(boolean utland) throws IOException {
        return engangssøknad(utland, fremtidigFødsel(), norskForelder());
    }

    public static Søknad engangssøknad(RelasjonTilBarn relasjon) throws IOException {
        return engangssøknad(false, relasjon, norskForelder());
    }

    public static Søknad engangssøknad(boolean utland, RelasjonTilBarn relasjon, AnnenForelder annenForelder,
            Vedlegg... vedlegg) throws IOException {
        Søknad s = new Søknad(LocalDateTime.now(), søker(), engangstønad(utland, relasjon, annenForelder), vedlegg);
        s.setBegrunnelseForSenSøknad("Glemte hele ungen");
        s.setTilleggsopplysninger("Intet å tilføye");
        return s;
    }

    public static Engangsstønad engangstønad(boolean utland, RelasjonTilBarn relasjon, AnnenForelder annenForelder) {
        Engangsstønad stønad = new Engangsstønad(medlemsskap(utland), relasjon);
        stønad.setAnnenForelder(annenForelder);
        return stønad;
    }

    public static Utenlandsopphold utenlandsopphold() {
        return new Utenlandsopphold(CountryCode.SE, varighet());
    }

    public static NorskForelder norskForelder() {
        return new NorskForelder(true, farnavn(), fnr());
    }

    public static UtenlandskForelder utenlandskForelder() {
        return new UtenlandskForelder(true, farnavn(), "123456", CountryCode.SE);
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
        utenlandOpphold.add(new Utenlandsopphold(CountryCode.SE,
                new LukketPeriode(LocalDate.now().minusYears(1), LocalDate.now().minusMonths(6))));
        utenlandOpphold.add(new Utenlandsopphold(CountryCode.FI,
                new LukketPeriode(LocalDate.now().minusMonths(6), LocalDate.now())));
        return new TidligereOppholdsInformasjon(false, ArbeidsInformasjon.ARBEIDET_I_UTLANDET, utenlandOpphold);
    }

    static TidligereOppholdsInformasjon tidligereOppHoldINorge() {
        return new TidligereOppholdsInformasjon(true, ArbeidsInformasjon.ARBEIDET_I_NORGE, Collections.emptyList());
    }

    public static Omsorgsovertakelse omsorgsovertakelse() {
        Omsorgsovertakelse overtakelse = new Omsorgsovertakelse(nå(), OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE,
                forrigeMåned());
        overtakelse.setBeskrivelse("dette er en beskrivelse");
        return overtakelse;
    }

    public static PåkrevdVedlegg påkrevdVedlegg() {
        return påkrevdVedlegg("terminbekreftelse.pdf");
    }

    public static ValgfrittVedlegg valgfrittVedlegg() {
        return valgfrittVedlegg("terminbekreftelse.pdf");
    }

    public static PåkrevdVedlegg påkrevdVedlegg(String name) {
        try {
            return new PåkrevdVedlegg(TERMINBEKREFTELSE, new ClassPathResource(name));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static ValgfrittVedlegg valgfrittVedlegg(String name) {
        try {
            return new ValgfrittVedlegg(TERMINBEKREFTELSE, new ClassPathResource(name));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Adopsjon adopsjon() {
        return new Adopsjon(nå(), false, 1, nå(), nå());
    }

    public static Fødsel fødsel() {
        return fødsel(forrigeMåned());
    }

    public static Fødsel fødsel(LocalDate date) {
        return new Fødsel(date);
    }

    public static FramtidigOppholdsInformasjon framtidigOppHoldIUtlandet() {
        List<Utenlandsopphold> opphold = new ArrayList<>();
        opphold.add(new Utenlandsopphold(CountryCode.GR,
                new LukketPeriode(LocalDate.now(), LocalDate.now().plusMonths(6))));
        opphold.add(new Utenlandsopphold(CountryCode.DE,
                new LukketPeriode(LocalDate.now().plusMonths(6), LocalDate.now().plusYears(1))));
        return new FramtidigOppholdsInformasjon(true, false, opphold);
    }

    public static FramtidigOppholdsInformasjon framtidigOppholdINorge() {
        return new FramtidigOppholdsInformasjon(true, true, Collections.emptyList());
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
        return new Søker(fnr(), aktoer(), BrukerRolle.MOR, navn);
    }

    private static Navn navn() {
        return new Navn("Mor", "Godhjerta", "Morsen");
    }

    private static Navn farnavn() {
        return new Navn("Far", "Faraday", "Farsken");
    }

    public static FremtidigFødsel fremtidigFødsel() {
        return new FremtidigFødsel(nesteMåned(), forrigeMåned());
    }

    public static LukketPeriode varighet() {
        return new LukketPeriode(nå(), nesteMåned());
    }

    public static LocalDate nesteMåned() {
        return nå().plus(enMåned());
    }

    static LocalDate forrigeMåned() {
        return nå().minus(enMåned());
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
        return new Fødselsnummer("01010133333");
    }

    public static Navn navnUtenMellomnavn() {
        return new Navn("Mor", null, "Monsen");
    }

    public static AnnenForelder ukjentForelder() {
        return new UkjentForelder();
    }
}
