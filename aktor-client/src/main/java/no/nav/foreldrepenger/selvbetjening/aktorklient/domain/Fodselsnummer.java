package no.nav.foreldrepenger.selvbetjening.aktorklient.domain;

public class Fodselsnummer {

   private final String digits;

   public Fodselsnummer(String digits) {
      this.digits = digits;
   }

   public String digits() {
      return digits;
   }

   @Override
   public String toString() {
      return "Fodselsnummer [digits=" + digits + "]";
   }

}
