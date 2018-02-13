package no.nav.foreldrepenger.mottak.pdf;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class SøknadsinfoExtractor {

   private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.uuuu");

   public static String userId(SoeknadsskjemaEngangsstoenad soknad) {
      if (soknad.getBruker() instanceof AktoerId) {
         AktoerId aktor = (AktoerId) soknad.getBruker();
         return aktor.getAktoerId();
      } else if (soknad.getBruker() instanceof Bruker) {
         Bruker bruker = ((Bruker) soknad.getBruker());
         return bruker.getPersonidentifikator();
      } else {
         throw new IllegalArgumentException("I don't know how to handle user of type " +
            soknad.getBruker().getClass().getSimpleName());
      }
   }

   public static String tidligereUtenlandsopphold(SoeknadsskjemaEngangsstoenad soknad) {
      if (soknad.getTilknytningNorge() == null || soknad.getTilknytningNorge().getTidligereOppholdUtenlands() == null) {
         return "Ingen";
      }

      return Optional.ofNullable(soknad.getTilknytningNorge())
         .map(TilknytningNorge::getTidligereOppholdUtenlands)
         .map(t -> t.getUtenlandsoppholds())
         .map(SøknadsinfoExtractor::formatOppholdList)
         .orElse("Norge");
   }


   public static int childCount(SoeknadsskjemaEngangsstoenad soknad) {
      return Optional.ofNullable(soknad.getOpplysningerOmBarn())
         .map(OpplysningerOmBarn::getAntallBarn)
         .orElse(0);
   }

   public static String termindato(SoeknadsskjemaEngangsstoenad soknad) {
      return Optional.ofNullable(soknad.getOpplysningerOmBarn())
         .map(OpplysningerOmBarn::getTermindato)
         .map(d -> d.format(DATE_FMT))
         .orElse("ukjent");
   }

   public static String terminbekreftelsesDato(SoeknadsskjemaEngangsstoenad soknad) {
      return Optional.ofNullable(soknad.getOpplysningerOmBarn())
         .map(OpplysningerOmBarn::getTerminbekreftelsedato)
         .map(d -> d.format(DATE_FMT))
         .orElse("ukjent");
   }

   public static long vedleggCount(SoeknadsskjemaEngangsstoenad soknad) {
      return Optional.ofNullable(soknad.getVedleggListe())
         .map(v -> v.getVedleggs().stream())
         .map(Stream::count)
         .orElse(0L);
   }

   public static String countryName(Boolean b) {
      return b != null && b ? "Norge" : "utlandet";
   }

   private static String formatOppholdList(List<Utenlandsopphold> opphold) {
      return opphold.stream()
         .map(SøknadsinfoExtractor::formatOpphold)
         .collect(joining("\n"));
   }

   private static String formatOpphold(Utenlandsopphold opphold) {
      return opphold.getLand().getValue() + ": " + opphold.getPeriode().getFom().format(DATE_FMT) + " - " +
         opphold.getPeriode().getTom().format(DATE_FMT);
   }

}
