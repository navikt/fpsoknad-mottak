package no.nav.foreldrepenger.mottak.error;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import com.google.common.collect.Lists;

import no.nav.foreldrepenger.mottak.util.StringUtil;
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

    private ResponseEntity<Object> logAndHandle(HttpStatus status, Exception e, WebRequest req, Object... messages) {
        return logAndHandle(status, e, req, new HttpHeaders(), messages);
    }

    private ResponseEntity<Object> logAndHandle(HttpStatus status, Exception e, WebRequest req, HttpHeaders headers,
            Object... messages) {
        return logAndHandle(status, e, req, headers, asList(messages));
    }

    private ResponseEntity<Object> logAndHandle(HttpStatus status, Exception e, WebRequest req, HttpHeaders headers,
            List<Object> messages) {
        var apiError = apiErrorFra(status, req, e, messages);
        LOG.warn("({}) {} {} ({})", subject(), status, apiError.getMessages(), status.value(), e);
        return handleExceptionInternal(e, apiError, headers, status, req);
    }

    private String subject() {
        return Optional.ofNullable(tokenUtil.getSubject())
                .map(StringUtil::mask)
                .orElse("Uautentisert");
    }

    private static ApiError apiErrorFra(HttpStatus status, WebRequest req, Exception e, List<Object> messages) {
        var uri = URI.create(ServletWebRequest.class.cast(req).getRequest().getRequestURI());
        var problem = Problem.builder() // TODO maybe later
                .withType(uri)
                .withDetail(getMostSpecificCause(e).getMessage())
                .withStatus(new HttpStatusAdapter(status))
                .with("uuid", callId());
        var msgs = messages(e, messages);
        // return !msgs.isBlank() ? problem.with("messages", msgs).build() :
        // problem.build();
        return new ApiError(status, e, messages);
    }

    private static String messages(Throwable t, List<Object> objects) {
        List<Object> messages = Lists.newArrayList(objects);
        return messages.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(","));
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
