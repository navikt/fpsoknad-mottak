package no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold;

import java.util.Optional;

public interface OrganisasjonClient {

    void ping();

    Optional<String> nameFor(String orgnr);

}
