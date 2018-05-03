package no.nav.foreldrepenger.oppslag.http.lookup.medl;

import no.nav.foreldrepenger.oppslag.http.lookup.person.Fodselsnummer;

import java.util.List;

public interface MedlClient {

    void ping();

    List<MedlPeriode> medlInfo(Fodselsnummer fnr);
}
