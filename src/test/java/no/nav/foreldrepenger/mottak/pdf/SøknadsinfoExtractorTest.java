package no.nav.foreldrepenger.mottak.pdf;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.*;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class SøknadsinfoExtractorTest {

   @Test
   public void userIdAktoer() {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      AktoerId aktor = new AktoerId();
      aktor.setAktoerId("aktøren");
      soknad.setBruker(aktor);
      String userId = SøknadsinfoExtractor.userId(soknad);
      assertEquals("aktøren", userId);
   }

   @Test
   public void userIdBruker() {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      Bruker bruker = new Bruker();
      bruker.setPersonidentifikator("identifikatoren");
      soknad.setBruker(bruker);
      String userId = SøknadsinfoExtractor.userId(soknad);
      assertEquals("identifikatoren", userId);
   }

   @Test
   public void harTidligereUtenlandsopphold() {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      TilknytningNorge tilknytning = new TilknytningNorge();
      TilknytningNorge.TidligereOppholdUtenlands oppholdUtenlands = new TilknytningNorge.TidligereOppholdUtenlands();
      Utenlandsopphold opphold = new Utenlandsopphold();
      Periode periode = new Periode();
      LocalDate now = LocalDate.now();
      periode.setFom(now.minusMonths(2));
      periode.setTom(now);
      opphold.setPeriode(periode);
      Landkoder land = new Landkoder();
      land.setValue("Utlandet");
      opphold.setLand(land);
      oppholdUtenlands.withUtenlandsopphold(opphold);
      tilknytning.setTidligereOppholdUtenlands(oppholdUtenlands);
      soknad.setTilknytningNorge(tilknytning);
      String actual = SøknadsinfoExtractor.tidligereUtenlandsopphold(soknad);
      assertEquals("Utlandet: 13.12.2017 - 13.02.2018", actual);
   }

   @Test
   public void ingenTidligereUtenlandsopphold() {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      String actual = SøknadsinfoExtractor.tidligereUtenlandsopphold(soknad);
      assertEquals("Ingen", actual);
   }

   @Test
   public void childCount() {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      OpplysningerOmBarn barn = new OpplysningerOmBarn();
      barn.setAntallBarn(2);
      soknad.setOpplysningerOmBarn(barn);
      int actual = SøknadsinfoExtractor.childCount(soknad);
      assertEquals(2, actual);
   }

   @Test
   public void termindatoSet() {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      OpplysningerOmBarn barn = new OpplysningerOmBarn();
      LocalDate date = LocalDate.of(2018, 2, 13);
      barn.setTermindato(date);
      soknad.setOpplysningerOmBarn(barn);
      String actual = SøknadsinfoExtractor.termindato(soknad);
      assertEquals("13.02.2018", actual);
   }

   @Test
   public void termindatoNotSet() {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      String actual = SøknadsinfoExtractor.termindato(soknad);
      assertEquals("ukjent", actual);
   }

   @Test
   public void terminbekreftelsesdatoSet() {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      OpplysningerOmBarn barn = new OpplysningerOmBarn();
      LocalDate date = LocalDate.of(2018, 2, 13);
      barn.setTerminbekreftelsedato(date);
      soknad.setOpplysningerOmBarn(barn);
      String actual = SøknadsinfoExtractor.terminbekreftelsesDato(soknad);
      assertEquals("13.02.2018", actual);
   }

   @Test
   public void terminbekreftelsesdatoNotSet() {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      String actual = SøknadsinfoExtractor.terminbekreftelsesDato(soknad);
      assertEquals("ukjent", actual);
   }

   @Test
   public void vedleggSet() {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      VedleggListe vedleggListe = new VedleggListe();
      vedleggListe.withVedlegg(new Vedlegg());
      soknad.setVedleggListe(vedleggListe);
      long actual = SøknadsinfoExtractor.vedleggCount(soknad);
      assertEquals(1, actual);
   }

   @Test
   public void vedleggNotSet() {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      long actual = SøknadsinfoExtractor.vedleggCount(soknad);
      assertEquals(0L, actual);
   }

}
