package no.nav.foreldrepenger.oppslag.inntekt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeFactory;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import no.nav.foreldrepenger.oppslag.domain.Inntekt;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.AktoerId;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Organisasjon;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Periode;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.PersonIdent;

class InntektMapperTest {

   @ParameterizedTest
   @MethodSource("valueProvider")
   public void allValuesSet(
         String virksomhetId,
         no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt inntekt) {
      Inntekt expected = new Inntekt(
         CalendarConverter.toLocalDate(inntekt.getOpptjeningsperiode().getStartDato()),
         Optional.of(inntekt.getOpptjeningsperiode().getSluttDato()).map(CalendarConverter::toLocalDate),
         inntekt.getBeloep().doubleValue(),
         virksomhetId);
      Inntekt actual = InntektMapper.map(inntekt);
      assertEquals(expected, actual);
   }

   static Stream<Arguments> valueProvider() throws Exception {
      no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt fraOrg =
         new no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt();
      Periode periode = new Periode();
      periode.setStartDato(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13"));
      periode.setSluttDato(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-14"));
      fraOrg.setOpptjeningsperiode(periode);
      fraOrg.setBeloep(BigDecimal.valueOf(1234.5));
      Organisasjon org = new Organisasjon();
      org.setOrgnummer("11111");
      fraOrg.setVirksomhet(org);

      no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt fraPerson =
         new no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt();
      periode = new Periode();
      periode.setStartDato(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-15"));
      periode.setSluttDato(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-16"));
      fraPerson.setOpptjeningsperiode(periode);
      fraPerson.setBeloep(BigDecimal.valueOf(1235.6));
      PersonIdent person = new PersonIdent();
      person.setPersonIdent("22222");
      fraPerson.setVirksomhet(person);

      no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt fraAktoer =
         new no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt();
      periode = new Periode();
      periode.setStartDato(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-17"));
      periode.setSluttDato(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-18"));
      fraAktoer.setOpptjeningsperiode(periode);
      fraAktoer.setBeloep(BigDecimal.valueOf(1236.7));
      AktoerId aktoerId = new AktoerId();
      aktoerId.setAktoerId("33333");
      fraAktoer.setVirksomhet(aktoerId);

      return Stream.of(
         Arguments.of("11111", fraOrg),
         Arguments.of("22222", fraPerson),
         Arguments.of("33333", fraAktoer)
      );
   }

}
