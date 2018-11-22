package no.nav.foreldrepenger.mottak.http.errorhandling;

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.http.Constants.NAV_CALL_ID;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiError {

    private final HttpStatus status;
    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timestamp;
    @JsonFormat(with = WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
    private final List<String> messages;
    private final String uuid;

    ApiError(HttpStatus status, Throwable t) {
        this(status, t, null);
    }

    ApiError(HttpStatus status, Throwable t, List<Object> objects) {
        this(status, t, null, objects);
    }

    ApiError(HttpStatus status, Throwable t, String destination, List<Object> objects) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.messages = messages(t, objects);
        this.uuid = MDC.get(NAV_CALL_ID);
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

    private static String getRootCauseMessage(Throwable t) {
        return getMostSpecificCause(t).getMessage();
    }

    private static ImmutableList<String> messages(Throwable t, List<Object> objects) {
        Builder<String> builder = new ImmutableList.Builder<>();
        String msg = getRootCauseMessage(t);
        if (msg != null) {
            builder.add(msg);
        }
        return builder.add(getRootCauseMessage(t))
                .addAll(objects.stream()
                        .filter(s -> s != null)
                        .map(Object::toString)
                        .collect(toList()))
                .build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[status=" + status + ", timestamp=" + timestamp + ", messages=" + messages
                + ", uuid=" + uuid + "]";
    }
}