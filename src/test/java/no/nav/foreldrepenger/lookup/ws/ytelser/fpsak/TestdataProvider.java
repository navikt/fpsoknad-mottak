package no.nav.foreldrepenger.lookup.ws.ytelser.fpsak;

import no.nav.foreldrepenger.time.DateUtil;
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
      sak.setOpprettet(DateUtil.toXMLGregorianCalendar(LocalDate.of(2017, 12, 13)));
      return sak;
   }

}
