package no.nav.foreldrepenger.mottak.error;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import no.nav.foreldrepenger.common.error.SøknadEgenskapException;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.http.TokenUtil;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;

@ControllerAdvice
public class MottakExceptionHandler extends ResponseEntityExceptionHandler {
    private final TokenUtil tokenUtil;
    private static final Logger LOG = LoggerFactory.getLogger(MottakExceptionHandler.class);
    private static final Logger SECURE_LOG = LoggerFactory.getLogger("secureLogger");

    public MottakExceptionHandler(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest req) {
        return logAndHandle(UNPROCESSABLE_ENTITY, e, req, validationErrors(e));
    }

    private static List<String> validationErrors(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getField)
            .toList();
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
    protected ResponseEntity<Object> handleValidationException(ConstraintViolationException e, WebRequest req) {
        return logAndHandle(BAD_REQUEST, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUnauthorizedException(JwtTokenUnauthorizedException e, WebRequest req) {
        return logAndHandle(UNAUTHORIZED, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUnauthenticatedJwtException(JwtTokenValidatorException e, WebRequest req) {
        return logAndHandle(FORBIDDEN, e, req);
    }


    /**
     * Håndtering av klient exceptions
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleWebClientResponseException(WebClientRequestException e, WebRequest request) {
        return logAndHandle(BAD_REQUEST, e, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException e, WebRequest request) {
        return logAndHandle(e.getStatusCode(), e, request);
    }


    /**
     * Håndtering av exceptions ikke definert over
     */
    @ExceptionHandler
    protected ResponseEntity<Object> handleUncaught(Exception e, WebRequest req) {
        return logAndHandle(INTERNAL_SERVER_ERROR, e, req);
    }

    private ResponseEntity<Object> logAndHandle(HttpStatusCode status, Exception e, WebRequest req, Object... messages) {
        return logAndHandle(status, e, req, asList(messages));
    }

    private ResponseEntity<Object> logAndHandle(HttpStatusCode status, Exception e, WebRequest req, List<Object> messages) {
        var apiError = apiErrorFra(status, e, messages);
        if (ikkeLoggExceptionsMedSensitiveOpplysnignerTilVanligLogg(e)) {
            LOG.warn("[{} ({})] {}", req.getContextPath(), status, apiError.getMessages());
            SECURE_LOG.warn("[{}] {} {}", req.getContextPath(), status, apiError.getMessages(), e);
        } else if (tokenUtil.erInnloggetBruker() && !tokenUtil.erUtløpt()) {
            LOG.warn("[{}] {} {}", req.getContextPath(), status, apiError.getMessages(), e);
        } else {
            LOG.debug("[{}] {} {}", req.getContextPath(), status, apiError.getMessages(), e);
        }
        return handleExceptionInternal(e, apiError, new HttpHeaders(), status, req);
    }

    private static ApiError apiErrorFra(HttpStatusCode status, Exception e, List<Object> messages) {
        return new ApiError(status, e, messages);
    }

    private boolean ikkeLoggExceptionsMedSensitiveOpplysnignerTilVanligLogg(Exception e) {
        return e instanceof MethodArgumentNotValidException || e instanceof ConstraintViolationException;
    }

}
