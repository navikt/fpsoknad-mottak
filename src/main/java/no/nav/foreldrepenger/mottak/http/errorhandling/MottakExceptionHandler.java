package no.nav.foreldrepenger.mottak.http.errorhandling;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import no.nav.foreldrepenger.mottak.innsending.engangsstønad.DokmotQueueUnavailableException;
import no.nav.foreldrepenger.mottak.util.TokenHelper;
import no.nav.security.oidc.exceptions.OIDCTokenValidatorException;
import no.nav.security.spring.oidc.validation.interceptor.OIDCUnauthorizedException;

@ControllerAdvice
public class MottakExceptionHandler extends ResponseEntityExceptionHandler {

    @Inject
    private TokenHelper tokenHelper;

    private static final Logger LOG = LoggerFactory.getLogger(MottakExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<Object> handleHttpStatusCodeException(HttpStatusCodeException e, WebRequest request) {
        if (e.getStatusCode().equals(UNAUTHORIZED) || e.getStatusCode().equals(FORBIDDEN)) {
            return logAndHandle(e.getStatusCode(), e, request, tokenHelper.getExpiryDate());
        }
        return logAndHandle(e.getStatusCode(), e, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatus status, WebRequest req) {
        return logAndHandle(UNPROCESSABLE_ENTITY, e, req, headers, validationErrors(e));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
            HttpHeaders headers, HttpStatus status, WebRequest req) {
        return logAndHandle(UNPROCESSABLE_ENTITY, e, req, headers);
    }

    @ExceptionHandler(value = { DokmotQueueUnavailableException.class })
    protected ResponseEntity<Object> handleRemoteUnavailable(DokmotQueueUnavailableException e, WebRequest req) {
        return logAndHandle(INTERNAL_SERVER_ERROR, e, req);
    }

    @ExceptionHandler({ OIDCUnauthorizedException.class })
    public ResponseEntity<Object> handleUnauthorizedException(OIDCUnauthorizedException e, WebRequest req) {
        return logAndHandle(UNAUTHORIZED, e, req);
    }

    @ExceptionHandler({ OIDCTokenValidatorException.class })
    public ResponseEntity<Object> handleUnauthenticatedOIDCException(OIDCTokenValidatorException e, WebRequest req) {
        return logAndHandle(FORBIDDEN, e, req, e.getExpiryDate());
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<Object> handleUncaught(Exception e, WebRequest req) {
        return logAndHandle(INTERNAL_SERVER_ERROR, e, req);
    }

    private ResponseEntity<Object> logAndHandle(HttpStatus status, Exception e, WebRequest req, Object... messages) {
        return logAndHandle(status, e, req, new HttpHeaders(), messages);
    }

    private ResponseEntity<Object> logAndHandle(HttpStatus status, Exception e, WebRequest req, HttpHeaders headers,
            Object... messages) {
        return logAndHandle(status, e, req, headers, asList(messages));
    }

    private ResponseEntity<Object> logAndHandle(HttpStatus status, Exception e, WebRequest req, HttpHeaders headers,
            List<Object> messages) {
        ApiError apiError = apiErrorFra(status, e, req, messages);
        LOG.warn("{}", apiError.getMessages(), e);
        return handleExceptionInternal(e, apiError, headers, status, req);
    }

    private static ApiError apiErrorFra(HttpStatus status, Exception e, WebRequest req, List<Object> messages) {
        return req instanceof ServletWebRequest ? new ApiError(status, e, destFra(req), messages)
                : new ApiError(status, e, messages);
    }

    private static String destFra(WebRequest req) {
        return ServletWebRequest.class.cast(req).getRequest().getRequestURI();
    }

    private static List<String> validationErrors(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors()
                .stream()
                .map(MottakExceptionHandler::errorMessage)
                .collect(toList());
    }

    private static String errorMessage(FieldError error) {
        return error.getField() + " " + error.getDefaultMessage();
    }
}
