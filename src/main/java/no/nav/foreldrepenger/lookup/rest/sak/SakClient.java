package no.nav.foreldrepenger.lookup.rest.sak;

import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;

import java.util.List;

public interface SakClient {

    List<no.nav.foreldrepenger.lookup.ws.ytelser.Sak> sakerFor(Fødselsnummer fnr, String oidcToken);

}
