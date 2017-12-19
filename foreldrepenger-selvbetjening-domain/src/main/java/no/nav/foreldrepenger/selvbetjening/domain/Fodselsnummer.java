package no.nav.foreldrepenger.selvbetjening.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Fodselsnummer {

   private final String fnr;

   @JsonCreator
   public Fodselsnummer(String fnr) {
      this.fnr = fnr;
   }

   public String getFnr() {
      return fnr;
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + " [fnr=" + fnr + "]";
   }

}
