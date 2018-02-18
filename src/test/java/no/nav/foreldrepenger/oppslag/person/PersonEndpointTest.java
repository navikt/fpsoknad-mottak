package no.nav.foreldrepenger.oppslag.person;

import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Person;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("slow")
public class PersonEndpointTest {

   @MockBean
   private PersonV3 personV3;

   @MockBean
   private AktorIdClient aktorIdClient;

   @BeforeEach
   public void setup() throws Exception {
      AktorId aktorId = new AktorId("12345");
      when(aktorIdClient.aktorIdForFnr(any(Fodselsnummer.class)))
         .thenReturn(aktorId);

      HentPersonResponse response = new HentPersonResponse();
      response.setPerson(person());
      when(personV3.hentPerson(any(HentPersonRequest.class)))
         .thenReturn(response);
   }

   @Autowired
   private TestRestTemplate restTemplate;

   @Test
   public void makeHttpRequestAndDeserializeResult() {

      Person person =
         restTemplate.getForObject("/person/?fnr=12345678910", Person.class);
      assertNotNull(person);
   }

   private no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person() {
      no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Person();
      PersonIdent personIdent = new PersonIdent();
      NorskIdent norskIdent = new NorskIdent();
      Personidenter personidenter = new Personidenter();
      personidenter.setValue("FNR");
      norskIdent.setType(personidenter);
      norskIdent.setIdent("12345");
      personIdent.setIdent(norskIdent);
      person.setAktoer(personIdent);
      Statsborgerskap statsborgerskap = new Statsborgerskap();
      Landkoder land = new Landkoder();
      land.setValue("Mordor");
      statsborgerskap.setLand(land);
      person.setStatsborgerskap(statsborgerskap);
      Kjoenn kjoenn = new Kjoenn();
      Kjoennstyper kjoennstyper = new Kjoennstyper();
      kjoennstyper.setValue("M");
      kjoenn.setKjoenn(kjoennstyper);
      person.setKjoenn(kjoenn);
      Personnavn personnavn = new Personnavn();
      personnavn.setFornavn("Hans");
      personnavn.setEtternavn("Hansen");
      person.setPersonnavn(personnavn);
      Bostedsadresse bostedsadresse = new Bostedsadresse();
      Gateadresse gateadresse = new Gateadresse();
      gateadresse.setGatenavn("Storgata");
      gateadresse.setHusnummer(1);
      gateadresse.setTilleggsadresseType("OFFISIELL ADRESSE");
      Landkoder landkoder = new Landkoder();
      landkoder.setValue("NO");
      gateadresse.setLandkode(landkoder);
      Postnummer postnummer = new Postnummer();
      postnummer.setValue("0123");
      gateadresse.setPoststed(postnummer);
      bostedsadresse.setStrukturertAdresse(gateadresse);
      person.setBostedsadresse(bostedsadresse);
      Foedselsdato foedselsdato = new Foedselsdato();
      foedselsdato.setFoedselsdato(CalendarConverter.toXMLGregorianCalendar(LocalDate.of(2018, 02, 18)));
      person.setFoedselsdato(foedselsdato);
      return person;
   }

}
