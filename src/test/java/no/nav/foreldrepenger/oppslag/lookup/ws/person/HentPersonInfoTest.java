package no.nav.foreldrepenger.oppslag.lookup.ws.person;


import no.nav.foreldrepenger.oppslag.lookup.ws.aktor.AktorId;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;

import static java.time.LocalDate.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HentPersonInfoTest {

    private PersonClientTpsWs klient;

    @Mock
    private Barnutvelger barnutvelger;

    @Mock
    private PersonV3 tps;

    @Before
    public void setUp() {
        klient = new PersonClientTpsWs(tps, barnutvelger);
    }

    @Test
    public void testHentingAvPersonMedBarnSkalKallePåTpsToGanger() throws Exception {
        when(tps.hentPerson(any())).thenReturn(response(barn("11111898765", "FNR")));

        klient.hentPersonInfo(id());
        verify(tps, times(2)).hentPerson(any());
    }

    @Test
    public void testHentingAvPersonMedFdatBarnSkalKallePåTpsEnGang() throws Exception {
        when(tps.hentPerson(any())).thenReturn(response(barn("11111800000", "FDAT")));

        klient.hentPersonInfo(id());
        verify(tps, times(1)).hentPerson(any());
    }

    private ID id() {
        Fodselsnummer fnr = new Fodselsnummer("fnr");
        AktorId aktorId = new AktorId("aktør");
        return new ID(aktorId, fnr);
    }

    private HentPersonResponse response(Familierelasjon rel) {
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Person();
        person.setAktoer(aktoer("01018812345", "FNR"));
        person.setKjoenn(kjoenn());
        person.setPersonnavn(navn());
        person.setFoedselsdato(foedselsdato());

        if (rel != null) {
            person.getHarFraRolleI().add(rel);
        }

        HentPersonResponse response = new HentPersonResponse();
        response.setPerson(person);

        return response;
    }

    private Familierelasjon barn(String fnr, String fnrType) {
        Familierelasjon rel = new Familierelasjon();
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person barn = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Person();
        barn.setAktoer(aktoer(fnr, fnrType));
        rel.setTilPerson(barn);
        Familierelasjoner type = new Familierelasjoner();
        type.setValue("BARN");
        rel.setTilRolle(type);
        return rel;
    }

    private Foedselsdato foedselsdato() {
        Foedselsdato foedselsdato = new Foedselsdato();
        LocalDate twenty = now().minusYears(20);
        GregorianCalendar gcal = GregorianCalendar.from(twenty.atStartOfDay(ZoneId.systemDefault()));
        XMLGregorianCalendar xcal = null;
        try {
            xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (DatatypeConfigurationException ignored) {}
        foedselsdato.setFoedselsdato(xcal);
        return foedselsdato;
    }

    private Personnavn navn() {
        Personnavn navn = new Personnavn();
        navn.setFornavn("Test");
        navn.setEtternavn("Testesen");
        return navn;
    }

    private Kjoenn kjoenn() {
        Kjoenn kjoenn = new Kjoenn();
        Kjoennstyper type = new Kjoennstyper();
        type.setValue("K");
        kjoenn.setKjoenn(type);
        return kjoenn;
    }

    private Aktoer aktoer(String fnr, String fnrType) {
        PersonIdent aktoer = new PersonIdent();
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(fnr);
        Personidenter type = new Personidenter();
        type.setValue(fnrType);
        norskIdent.setType(type);
        aktoer.setIdent(norskIdent);

        return aktoer;
    }

}
