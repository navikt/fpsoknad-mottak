package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Component
public class PDLConnection {

    private static final Logger LOG = LoggerFactory.getLogger(PDLConnection.class);
    private final GraphQLWebClient client;
    private final TokenUtil tokenUtil;

    public PDLConnection(GraphQLWebClient client, TokenUtil tokenUtil) {
        this.client = client;
        this.tokenUtil = tokenUtil;
    }

    public Person hentPerson() {
        LOG.info("Henter person");
        return client.post("query-person.graphql", Map.of("id", tokenUtil.getSubject()), Person.class).block();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", client=" + client + "]";
    }

}
