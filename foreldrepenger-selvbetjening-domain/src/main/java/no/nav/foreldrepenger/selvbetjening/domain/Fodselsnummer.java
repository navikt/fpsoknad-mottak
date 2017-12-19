package no.nav.foreldrepenger.selvbetjening.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Fodselsnummer {

   private final String value;

   @JsonCreator
   public Fodselsnummer(String value) {
      this.value = value;
   }

   public String getValue() {
      return value;
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + " [value=" + value + "]";
   }

}
