package no.nav.foreldrepenger.oppslag.aareg;

import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static no.nav.foreldrepenger.oppslag.aareg.TestdataProvider.forhold;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArbeidsforholdMapperTest {

   @ParameterizedTest
   @MethodSource("valueProvider")
   @Tag("fast")
   public void mapValues(String arbeidsgiverId, String arbeidsgiverIdType, Aktoer aktoer) {
      no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold forhold = forhold(aktoer);

      LocalDate now = LocalDate.now();
      LocalDate earlier = now.minusMonths(2);
      Arbeidsforhold expected =
         new Arbeidsforhold(arbeidsgiverId, arbeidsgiverIdType, "yrke1/yrke2", earlier, Optional.of(now));
      Arbeidsforhold actual = ArbeidsforholdMapper.map(forhold);

      assertEquals(expected, actual);
   }

   static Stream<Arguments> valueProvider() {
      Organisasjon orgUtenNavn = new Organisasjon();
      orgUtenNavn.setOrgnummer("12345");
      Organisasjon orgMedNavn = new Organisasjon();
      orgMedNavn.setNavn("el orgo");

      HistoriskArbeidsgiverMedArbeidsgivernummer histUtenNavn = new HistoriskArbeidsgiverMedArbeidsgivernummer();
      histUtenNavn.setArbeidsgivernummer("12346");
      HistoriskArbeidsgiverMedArbeidsgivernummer histMedNavn = new HistoriskArbeidsgiverMedArbeidsgivernummer();
      histMedNavn.setNavn("gamle saker");

      Person person = new Person();
      NorskIdent norskIdent = new NorskIdent();
      norskIdent.setIdent("12347");
      person.setIdent(norskIdent);

      return Stream.of(
         Arguments.of("12345", "orgnr", orgUtenNavn),
         Arguments.of("el orgo", "navn", orgMedNavn),
         Arguments.of("gamle saker", "navn", histMedNavn),
         Arguments.of("12346", "arbeidsgivernr", histUtenNavn),
         Arguments.of("12347", "fnr", person)
      );
   }

}
