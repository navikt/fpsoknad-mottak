package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import no.nav.foreldrepenger.mottak.dokmot.DokmotQueueUnavailableException;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelUnavailableException;

@ControllerAdvice
public class MottakExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(e, new ApiError(UNPROCESSABLE_ENTITY, responseBody(e), e), new HttpHeaders(),
                UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value = { DokmotQueueUnavailableException.class, FPFordelUnavailableException.class })
    protected ResponseEntity<Object> handleConflict(DokmotQueueUnavailableException e, WebRequest request) {
        return handleExceptionInternal(e, new ApiError(INTERNAL_SERVER_ERROR, ExceptionUtils.getRootCauseMessage(e), e),
                new HttpHeaders(),
                INTERNAL_SERVER_ERROR, request);
    }

    private static List<String> responseBody(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors()
                .stream()
                .map(MottakExceptionHandler::errorMessage)
                .collect(Collectors.toList());
    }

    private static String errorMessage(FieldError error) {
        return error.getDefaultMessage() + " (" + error.getField() + ")";
    }
}
