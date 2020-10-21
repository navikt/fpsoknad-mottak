package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.web.client.HttpClientErrorException.create;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import graphql.kickstart.spring.webclient.boot.GraphQLError;
import graphql.kickstart.spring.webclient.boot.GraphQLErrorsException;

@Component
public class PDLConvertingExceptionHandler implements PDLErrorResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PDLConvertingExceptionHandler.class);

    @Override
    public <T> T handle(GraphQLErrorsException e) {
        LOG.warn("PDL oppslag ga feil", e);
        throw safeStream(e.getErrors())
                .findFirst()
                .map(GraphQLError::getExtensions)
                .map(m -> m.get("code"))
                .filter(Objects::nonNull)
                .map(String.class::cast)
                .map(k -> exceptionFra(k, e.getMessage()))
                .orElse(new HttpServerErrorException(INTERNAL_SERVER_ERROR, e.getMessage(), null, null, null));
    }

    private static HttpStatusCodeException exceptionFra(String kode, String msg) {
        return switch (kode) {
            case "unauthenticated" -> e(UNAUTHORIZED, msg);
            case "unauthorized" -> e(FORBIDDEN, msg);
            case "bad_request" -> e(BAD_REQUEST, msg);
            case "not_found" -> e(NOT_FOUND, msg);
            default -> new HttpServerErrorException(INTERNAL_SERVER_ERROR, msg);
        };
    }

    private static HttpStatusCodeException e(HttpStatus status, String msg) {
        return create(status, msg, null, null, null);
    }
}
