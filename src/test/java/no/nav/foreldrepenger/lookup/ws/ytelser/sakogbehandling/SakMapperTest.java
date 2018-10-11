package no.nav.foreldrepenger.lookup.ws.ytelser.sakogbehandling;

import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import no.nav.foreldrepenger.time.DateUtil;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.Behandlingsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.Sakstemaer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SakMapperTest {

    @Test
    public void mapValues() {
        LocalDate now = LocalDate.now();
        no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak sbSak =
            sbsak(now);

        Sak expected = new Sak("sak1", "temaer",
            null, "sak1", "statusen", now);
        Sak actual = SakMapper.map(sbSak);

        assertEquals(expected, actual);
    }

    private no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak sbsak(LocalDate date) {
        no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak sbSak =
            new no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak();
        sbSak.setSaksId("sak1");
        sbSak.setOpprettet(DateUtil.toXMLGregorianCalendar(date));
        Sakstemaer sakstemaer = new Sakstemaer();
        sakstemaer.setValue("temaer");
        sbSak.setSakstema(sakstemaer);
        Behandlingskjede behKjede = new Behandlingskjede();
        Behandlingsstatuser statuser = new Behandlingsstatuser();
        statuser.setValue("statusen");
        behKjede.setSisteBehandlingsstatus(statuser);
        sbSak.getBehandlingskjede().add(behKjede);
        return sbSak;
    }

}
