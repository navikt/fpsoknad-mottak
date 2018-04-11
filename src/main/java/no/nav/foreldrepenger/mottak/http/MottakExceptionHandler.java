package no.nav.foreldrepenger.mottak.http;

import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import no.nav.foreldrepenger.mottak.dokmot.DokmotQueueConfig;
import no.nav.foreldrepenger.mottak.dokmot.DokmotQueueUnavailableException;

@ControllerAdvice
public class MottakExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = getLogger(MottakExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOG.error("Method argument not valid", e);
        return handleExceptionInternal(e, responseBody(e), new HttpHeaders(), UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value = { DokmotQueueUnavailableException.class })
    protected ResponseEntity<Object> handleConflict(DokmotQueueUnavailableException e, WebRequest request) {
        return handleExceptionInternal(e, unproxy(e.getConfig()), new HttpHeaders(), INTERNAL_SERVER_ERROR, request);
    }

    private static DokmotQueueConfig unproxy(DokmotQueueConfig proxied) {
        DokmotQueueConfig unwrapped = new DokmotQueueConfig();
        unwrapped.setChannelname(proxied.getChannelname());
        unwrapped.setHostname(proxied.getHostname());
        unwrapped.setPort(proxied.getPort());
        unwrapped.setQueuename(proxied.getQueuename());
        return unwrapped;
    }

    private static String responseBody(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors()
                .stream()
                .map(MottakExceptionHandler::errorMessage)
                .collect(joining("\n"));
    }

    private static String errorMessage(FieldError error) {
        return error.getDefaultMessage() + " (" + error.getField() + ")";
    }
}
