package no.nav.foreldrepenger.oppslag.fpsak;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Behandlingstema;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Sak;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Saksstatus;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SakMapperTest {

    @Test
    public void mapValues() throws Exception {
        Sak sak = new Sak();
        Behandlingstema tema = new Behandlingstema();
        tema.setTermnavn("temaet");
        sak.setBehandlingstema(tema);
        Saksstatus status = new Saksstatus();
        status.setTermnavn("statusen");
        sak.setStatus(status);
        sak.setOpprettet(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13"));
        Ytelse expected = new Ytelse("temaet",
                "statusen",
                LocalDate.of(2017, 12, 13),
                Optional.empty());
        Ytelse actual = SakMapper.map(sak);
        assertEquals(expected, actual);
    }

}
