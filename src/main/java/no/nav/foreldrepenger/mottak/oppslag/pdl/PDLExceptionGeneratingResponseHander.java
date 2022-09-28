package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConstants.FORBUDT;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConstants.IKKEFUNNET;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConstants.UAUTENTISERT;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConstants.UGYLDIG;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import graphql.kickstart.spring.webclient.boot.GraphQLError;
import graphql.kickstart.spring.webclient.boot.GraphQLErrorsException;

@Component
public class PDLExceptionGeneratingResponseHander implements PDLErrorResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PDLExceptionGeneratingResponseHander.class);

    @Override
    public <T> T handleError(GraphQLErrorsException e) {
        LOG.warn("PDL oppslag returnerte {} feil. {}", e.getErrors().size(), e.getErrors(), e);
        throw safeStream(e.getErrors())
                .findFirst() // TODO?
                .map(GraphQLError::getExtensions)
                .map(m -> m.get("code"))
                .filter(Objects::nonNull)
                .map(String.class::cast)
                .map(k -> exceptionFra(k, e.getMessage()))
                .orElse(new WebClientResponseException(INTERNAL_SERVER_ERROR.value(), e.getMessage(), null, null, null));
    }

    private static WebClientResponseException exceptionFra(String kode, String msg) {
        return switch (kode) {
            case UAUTENTISERT -> exception(UNAUTHORIZED, msg);
            case FORBUDT -> exception(FORBIDDEN, msg);
            case UGYLDIG -> exception(BAD_REQUEST, msg);
            case IKKEFUNNET -> exception(NOT_FOUND, msg);
            default -> exception(INTERNAL_SERVER_ERROR, msg);
        };
    }

    static WebClientResponseException exception(HttpStatus status, String msg) {
        return WebClientResponseException.create(status.value(), msg, null, null, null);
    }
}
