package no.nav.foreldrepenger.mottak.http;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timestamp;
    private final List<String> messages;
    private final String uuid;

    ApiError(HttpStatus status) {
        this(status, null);
    }

    ApiError(HttpStatus status, Throwable e) {
        this(status, e != null ? e.getMessage() : null, e);
    }

    ApiError(HttpStatus status, String message, Throwable e) {
        this(status, Collections.singletonList(message), e);
    }

    public ApiError(HttpStatus status, List<String> messages, Throwable e) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.messages = messages;
        this.uuid = MDC.get("X-Nav-CallId");
    }

    public String getUuid() {
        return uuid;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getMessages() {
        return messages;
    }

}