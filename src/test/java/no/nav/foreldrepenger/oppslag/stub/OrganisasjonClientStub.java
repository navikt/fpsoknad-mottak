package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold.OrganisasjonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class OrganisasjonClientStub implements OrganisasjonClient {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public Optional<String> nameFor(String orgnr) {
        return Optional.of("S. Vindel & sønn");
    }

}
