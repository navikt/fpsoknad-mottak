package no.nav.foreldrepenger.lookup.rest.sak;

import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;

import java.util.List;

public interface SakClient {

    List<no.nav.foreldrepenger.lookup.rest.sak.Sak> sakerFor(AktorId aktor, String oidcToken);

}
