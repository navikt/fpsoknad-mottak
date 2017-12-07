package no.nav.foreldrepenger.selvbetjening.aktorklient.domain;

public class Fodselsnummer {

   private final String fnr;

   public Fodselsnummer(String fnr) {
      this.fnr = fnr;
   }

   public String getValue() {
      return fnr;
   }

}
