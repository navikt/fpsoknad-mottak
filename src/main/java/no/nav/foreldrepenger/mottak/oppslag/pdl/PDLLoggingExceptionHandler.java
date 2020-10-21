package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import graphql.kickstart.spring.webclient.boot.GraphQLErrorsException;

@Component
@ConditionalOnMissingBean(PDLConvertingExceptionHandler.class)
public class PDLLoggingExceptionHandler implements PDLErrorResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PDLLoggingExceptionHandler.class);

    @Override
    public <T> T handle(GraphQLErrorsException e) {
        LOG.warn("GraphQL oppslag feilet", e.getErrors(), e);
        return null;
    }
}
