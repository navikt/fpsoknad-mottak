package no.nav.foreldrepenger.oppslag.lookup.ws.medl;

import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fodselsnummer;

import java.util.List;

public interface MedlClient {

    void ping();

    List<MedlPeriode> medlInfo(Fodselsnummer fnr);
}
