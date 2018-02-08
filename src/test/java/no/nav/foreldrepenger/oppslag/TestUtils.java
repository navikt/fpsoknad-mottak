package no.nav.foreldrepenger.oppslag;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;

import org.springframework.core.io.ClassPathResource;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.Bruker;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.Fodselsnummer;
import no.nav.foreldrepenger.mottak.domain.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.Fødsel;
import no.nav.foreldrepenger.mottak.domain.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.Medlemsskap;
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

public class TestUtils {
    static Søknad engangssøknad(boolean utland) throws IOException {
        return engangssøknad(utland, fremtidigFødsel());
    }

    static Søknad engangssøknad(boolean utland, RelasjonTilBarn relasjon) throws IOException {
        Søknad s = new Søknad(nå(), søker(), engangstønad(utland, relasjon), påkrevdVedlegg());
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
        overtakelse.setBeskrivelse("beskrivelse");
        overtakelse.setFødselsdato(forrigeMåned());
        return overtakelse;
    }

    static PåkrevdVedlegg påkrevdVedlegg() throws IOException {
        return påkrevdVedlegg("vedlegg.pdf");
    }

    static PåkrevdVedlegg påkrevdVedlegg(String name) throws IOException {
        return new PåkrevdVedlegg(Skjemanummer.N6, new ClassPathResource(name));
    }

    static Adopsjon adopsjon() {
        return new Adopsjon(nå(), false);
    }

    static Fødsel fødsel() {
        Fødsel fødsel = new Fødsel(forrigeMåned());
        return fødsel;
    }

    static FramtidigOppholdsInformasjon framtidigOppHoldIUtlandetHeleåret() {
        return new FramtidigOppholdsInformasjon(false, Collections.singletonList(new Utenlandsopphold(CountryCode.SE)));
    }

    static FramtidigOppholdsInformasjon framtidigOppholdINorge() {
        return new FramtidigOppholdsInformasjon(true, Collections.emptyList());
    }

    static Søker søker() {
        return søker(false);
    }

    static Søker søker(boolean isAktør) {
        return new Søker(isAktør ? aktoer() : fnr(), BrukerRolle.MOR);
    }

    static FremtidigFødsel fremtidigFødsel() {
        return new FremtidigFødsel(nå(), nesteMåned());
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
        return Period.ofMonths(1);
    }

    static LocalDate nå() {
        return LocalDate.now();
    }

    static LocalDate ettÅrSiden() {
        return LocalDate.now().minus(Period.ofYears(1));
    }

    static Bruker aktoer() {
        return new AktorId("11111111111111111");
    }

    static Bruker fnr() {
        return new Fodselsnummer("03016536325");
    }
}
