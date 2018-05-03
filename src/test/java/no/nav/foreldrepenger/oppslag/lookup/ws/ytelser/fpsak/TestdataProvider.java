package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.fpsak;

import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Behandlingstema;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Sak;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Saksstatus;

import java.time.LocalDate;

public class TestdataProvider {

   public static Sak sak() {
      Sak sak = new Sak();
      Behandlingstema tema = new Behandlingstema();
      tema.setTermnavn("temaet");
      sak.setBehandlingstema(tema);
      Saksstatus status = new Saksstatus();
      status.setTermnavn("statusen");
      sak.setStatus(status);
      sak.setOpprettet(CalendarConverter.toXMLGregorianCalendar(LocalDate.of(2017, 12, 13)));
      return sak;
   }

}
