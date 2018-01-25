package no.nav.foreldrepenger.oppslag.aareg;

import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArbeidsforholdMapperTest {

   @ParameterizedTest
   @MethodSource("valueProvider")
   public void mapValues(String arbeidsgiverId, String arbeidsgiverIdType, Aktoer aktoer) {
      no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold forhold = forhold(aktoer);

      LocalDate now = LocalDate.now();
      LocalDate earlier = now.minusMonths(2);
      Arbeidsforhold expected =
         new Arbeidsforhold(arbeidsgiverId, arbeidsgiverIdType, "yrke1/yrke2", earlier, Optional.of(now));
      Arbeidsforhold actual = ArbeidsforholdMapper.map(forhold);

      assertEquals(expected, actual);
   }

   private no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold forhold(Aktoer aktoer) {
      no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold forhold =
         new no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold();

      forhold.setArbeidsgiver(aktoer);

      Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();
      LocalDate now = LocalDate.now();
      LocalDate earlier = now.minusMonths(2);
      gyldighetsperiode.setFom(CalendarConverter.toXMLGregorianCalendar(earlier));
      gyldighetsperiode.setTom(CalendarConverter.toXMLGregorianCalendar(now));

      AnsettelsesPeriode ansettelsesperiode = new AnsettelsesPeriode();
      ansettelsesperiode.setPeriode(gyldighetsperiode);
      forhold.setAnsettelsesPeriode(ansettelsesperiode);

      Arbeidsforholdstyper type = new Arbeidsforholdstyper();
      type.setValue("typen");
      forhold.setArbeidsforholdstype(type);

      Arbeidsavtale avtale = new Arbeidsavtale();
      Yrker yrker = new Yrker();
      yrker.setValue("yrke1");
      avtale.setYrke(yrker);
      forhold.getArbeidsavtale().add(avtale);

      avtale = new Arbeidsavtale();
      yrker = new Yrker();
      yrker.setValue("yrke2");
      avtale.setYrke(yrker);
      forhold.getArbeidsavtale().add(avtale);
      return forhold;
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
