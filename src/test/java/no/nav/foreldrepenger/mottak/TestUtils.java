package no.nav.foreldrepenger.mottak;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collections;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.Fodselsnummer;
import no.nav.foreldrepenger.mottak.domain.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.Fødsel;
import no.nav.foreldrepenger.mottak.domain.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.OmsorgsOvertakelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.PåkrevdVedlegg;
import no.nav.foreldrepenger.mottak.domain.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.domain.Skjemanummer;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.Vedlegg;

public class TestUtils {

    public static boolean hasPdfSignature(byte[] bytes) {
        return bytes[0] == 0x25 &&
                bytes[1] == 0x50 &&
                bytes[2] == 0x44 &&
                bytes[3] == 0x46;
    }

    public static Søknad engangssøknad(boolean utland) throws IOException {
        return engangssøknad(utland, fremtidigFødsel());
    }

    static Søknad engangssøknad(boolean utland, RelasjonTilBarn relasjon, Vedlegg... vedlegg) throws IOException {
        Søknad s = new Søknad(LocalDateTime.now(), søker(), engangstønad(utland, relasjon), vedlegg);
        s.setBegrunnelseForSenSøknad("Glemte hele ungen");
        s.setTilleggsopplysninger("Intet å tilføye");
        return s;
    }

    static Engangsstønad engangstønad(boolean utland) {
        Engangsstønad stønad = engangstønad(utland, fremtidigFødsel());
        stønad.setAnnenForelder(norskForelder());
        return stønad;
    }

    static Engangsstønad engangstønad(boolean utland, RelasjonTilBarn relasjon) {
        return new Engangsstønad(medlemsskap(utland), relasjon);
    }

    static Utenlandsopphold utenlandsopphold() {
        return new Utenlandsopphold(CountryCode.SE, varighet());
    }

    static NorskForelder norskForelder() {
        return new NorskForelder(true, fnr());
    }

    static UtenlandskForelder utenlandskForelder() {
        return new UtenlandskForelder(true, CountryCode.SE);
    }

    static Medlemsskap medlemsskap() {
        return medlemsskap(false);
    }

    static Medlemsskap medlemsskap(boolean utland) {
        if (utland) {
            return new Medlemsskap(tidligereOppHoldIUtlandetHeleåret(), framtidigOppHoldIUtlandetHeleåret());
        }
        return new Medlemsskap(tidligereOppHoldINorge(), framtidigOppholdINorge());
    }

    static TidligereOppholdsInformasjon tidligereOppHoldIUtlandetHeleåret() {
        return new TidligereOppholdsInformasjon(false, ArbeidsInformasjon.ARBEIDET_I_UTLANDET,
                Collections.singletonList(new Utenlandsopphold(CountryCode.SE)));
    }

    static TidligereOppholdsInformasjon tidligereOppHoldINorge() {
        return new TidligereOppholdsInformasjon(true, ArbeidsInformasjon.ARBEIDET_I_NORGE, Collections.emptyList());
    }

    static Omsorgsovertakelse omsorgsovertakelse() {
        Omsorgsovertakelse overtakelse = new Omsorgsovertakelse(nå(), OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE);
        overtakelse.setBeskrivelse("dette er en beskrivelse");
        overtakelse.setFødselsdato(forrigeMåned());
        return overtakelse;
    }

    static PåkrevdVedlegg påkrevdVedlegg() throws IOException {
        return påkrevdVedlegg("terminbekreftelse.pdf");
    }

    static ValgfrittVedlegg valgfrittVedlegg() throws IOException {
        return valgfrittVedlegg("terminbekreftelse.pdf");
    }

    static PåkrevdVedlegg påkrevdVedlegg(String name) throws IOException {
        return new PåkrevdVedlegg(Skjemanummer.N6, new ClassPathResource(name));
    }

    static ValgfrittVedlegg valgfrittVedlegg(String name) throws IOException {
        return new ValgfrittVedlegg(Skjemanummer.N6, new ClassPathResource(name));
    }

    static Adopsjon adopsjon() {
        return new Adopsjon(nå(), false);
    }

    static Fødsel fødsel() {
        Fødsel fødsel = new Fødsel(forrigeMåned());
        return fødsel;
    }

    static FramtidigOppholdsInformasjon framtidigOppHoldIUtlandetHeleåret() {
        return new FramtidigOppholdsInformasjon(true, false,
                Collections.singletonList(new Utenlandsopphold(CountryCode.SE)));
    }

    static FramtidigOppholdsInformasjon framtidigOppholdINorge() {
        return new FramtidigOppholdsInformasjon(true, true, Collections.emptyList());
    }

    static String write(Object obj, boolean print, ObjectMapper mapper) throws JsonProcessingException {
        String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        if (print) {
            System.out.println(serialized);
            return serialized;
        }
        return serialized;
    }

    static Søker søker() {
        return søker(navn());
    }

    static Søker søker(Navn navn) {
        return new Søker(fnr(), aktoer(), BrukerRolle.MOR, navn);
    }

    private static Navn navn() {
        return new Navn("Mor", "Godhjerta", "Morsen");
    }

    static FremtidigFødsel fremtidigFødsel() {
        return new FremtidigFødsel(nesteMåned(), forrigeMåned());
    }

    static LukketPeriode varighet() {
        return new LukketPeriode(nå(), nesteMåned());
    }

    static LocalDate nesteMåned() {
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

    static LocalDate nå() {
        return LocalDate.now();
    }

    static LocalDate ettÅrSiden() {
        return LocalDate.now().minus(Period.ofYears(1));
    }

    static AktorId aktoer() {
        return new AktorId("11111111111111111");
    }

    static Fodselsnummer fnr() {
        return new Fodselsnummer("03016536325");
    }

    static Navn navnUtenMellomnavn() {
        return new Navn("Mor", null, "Monsen");
    }
}
