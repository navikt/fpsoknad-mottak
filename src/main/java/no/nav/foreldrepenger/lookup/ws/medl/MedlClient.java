package no.nav.foreldrepenger.lookup.ws.medl;

import java.util.List;

import no.nav.foreldrepenger.lookup.Pingable;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;

public interface MedlClient extends Pingable {

    List<MedlPeriode> medlInfo(Fødselsnummer fnr);
}
