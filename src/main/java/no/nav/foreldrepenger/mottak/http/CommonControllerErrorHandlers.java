package no.nav.foreldrepenger.mottak.http;

import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CommonControllerErrorHandlers extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        String bodyOfResponse = responseBody(ex);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private String responseBody(MethodArgumentNotValidException ex) {
        String bodyOfResponse = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(this::msg)
                .collect(Collectors.joining(","));
        return bodyOfResponse;
    }

    private String msg(FieldError s) {
        return s.getDefaultMessage();
    }

}
