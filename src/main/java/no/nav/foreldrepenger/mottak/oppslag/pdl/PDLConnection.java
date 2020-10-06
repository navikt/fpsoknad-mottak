package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;

@Component
public class PDLConnection {

    private static final Logger LOG = LoggerFactory.getLogger(PDLConnection.class);
    private final GraphQLWebClient client;

    public PDLConnection(GraphQLWebClient client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", client=" + client + "]";
    }

}
