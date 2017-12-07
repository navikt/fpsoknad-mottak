package no.nav.foreldrepenger.selvbetjening;

import java.util.Optional;

import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.AktorId;
import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.Fodselsnummer;

public interface AktorOperations {

   Optional<AktorId> aktorIdForFnr(Fodselsnummer fnr);

}
