package no.nav.foreldrepenger.mottak.error;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import no.nav.foreldrepenger.common.error.SøknadEgenskapException;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.util.TokenUtil;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;

@ControllerAdvice
public class MottakExceptionHandler extends ResponseEntityExceptionHandler {

    private final TokenUtil tokenUtil;
    private static final Logger LOG = LoggerFactory.getLogger(MottakExceptionHandler.class);

    public MottakExceptionHandler(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
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

    @ExceptionHandler
    public ResponseEntity<Object> handleHttpStatusCodeException(HttpStatusCodeException e, WebRequest request) {
        return logAndHandle(e.getStatusCode(), e, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUnauthorizedException(JwtTokenUnauthorizedException e, WebRequest req) {
        return logAndHandle(UNAUTHORIZED, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleVersionException(SøknadEgenskapException e, WebRequest req) {
        return logAndHandle(UNPROCESSABLE_ENTITY, e, req, e.getVersjon());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleIncompleteException(UnexpectedInputException e, WebRequest req) {
        return logAndHandle(UNPROCESSABLE_ENTITY, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUnauthenticatedJwtException(JwtTokenValidatorException e, WebRequest req) {
        return logAndHandle(FORBIDDEN, e, req);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleUncaught(Exception e, WebRequest req) {
        return logAndHandle(INTERNAL_SERVER_ERROR, e, req);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleValidationException(ConstraintViolationException e, WebRequest req) {
        return logAndHandle(BAD_REQUEST, e, req);
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
        var apiError = apiErrorFra(status, e, messages);
        if (tokenUtil.erAutentisert() && !tokenUtil.erUtløpt()) {
            LOG.warn("[{} ({})] {} {} ({})", req.getContextPath(), innloggetBruker(), status, apiError.getMessages(),
                    status.value(), e);
        } else {
            LOG.debug("[{}] {} {} ({})", req.getContextPath(), status, apiError.getMessages(),
                    status.value(), e);
        }
        return handleExceptionInternal(e, apiError, headers, status, req);
    }

    private String innloggetBruker() {
        return Optional.ofNullable(tokenUtil.fnr())
                .map(fnr -> fnr + " (" + tokenUtil.getExpiration() + ")")
                .orElse("Uautentisert");
    }

    private static ApiError apiErrorFra(HttpStatus status, Exception e, List<Object> messages) {
        return new ApiError(status, e, messages);
    }

    private static List<String> validationErrors(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .toList();
    }

}
