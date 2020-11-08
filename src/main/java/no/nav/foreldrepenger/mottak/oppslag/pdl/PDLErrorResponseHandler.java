package no.nav.foreldrepenger.mottak.oppslag.pdl;

import graphql.kickstart.spring.webclient.boot.GraphQLErrorsException;

interface PDLErrorResponseHandler {

    <T> T handleError(GraphQLErrorsException e);

}
